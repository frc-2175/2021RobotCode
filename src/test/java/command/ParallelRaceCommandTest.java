package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Before;

import frc.ServiceLocator;
import frc.command.Command;
import frc.command.ParallelRaceCommand;
import frc.logging.LogHandler;
import frc.logging.Logger;
import frc.logging.StdoutHandler;

public class ParallelRaceCommandTest {
    @Before
    public void before() {
        Logger robotLogger = new Logger(new LogHandler[] {
            new StdoutHandler()
        });

        ServiceLocator.clear();
        ServiceLocator.register(robotLogger);
    }

    @Test
    public void testInitialization() {
        TestCommand testCommand1 = new TestCommand();
        TestCommand testCommand2 = new TestCommand();
        Command[] commands = { testCommand1, testCommand2 };
        ParallelRaceCommand parcommand = new ParallelRaceCommand(commands);
        parcommand.init();
        assertTrue("The commads didn't initialize", testCommand1.getHasInitalized() && testCommand2.getHasInitalized());
    }

    @Test
    public void testExecution() {
        TestCommand testCommand1 = new TestCommand();
        TestCommand testCommand2 = new TestCommand();
        Command[] commands = { testCommand1, testCommand2 };
        ParallelRaceCommand parcommand = new ParallelRaceCommand(commands);

        parcommand.init();

        parcommand.execute();
        assertFalse("ParallelRaceCommand should not be finished until at least one of its commands is finished", parcommand.isFinished());
        assertEquals(1, testCommand1.getTimes());
        assertEquals(1, testCommand2.getTimes());

        testCommand2.setIsFinished(true);
        parcommand.execute();
        assertTrue("ParallelRaceCommand should be finished as soon as any child command is finished", parcommand.isFinished());
        assertEquals(2, testCommand1.getTimes());
        assertEquals(2, testCommand2.getTimes());

        parcommand.end();
        assertTrue("command 1 didn't end", testCommand1.getHasEnded());
        assertTrue("command 2 didn't end", testCommand2.getHasEnded());
    }

    @Test
	public void testEmpty() {
		ParallelRaceCommand parrallelraceommand = new ParallelRaceCommand();
		parrallelraceommand.init();
		parrallelraceommand.execute();
		assertTrue(parrallelraceommand.isFinished());
		parrallelraceommand.end();
	}

}
