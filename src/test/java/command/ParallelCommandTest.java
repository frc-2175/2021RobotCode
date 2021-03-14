package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Before;

import frc.ServiceLocator;
import frc.command.Command;
import frc.command.ParallelCommand;
import frc.logging.LogHandler;
import frc.logging.Logger;
import frc.logging.StdoutHandler;

public class ParallelCommandTest {
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
        ParallelCommand parcommand = new ParallelCommand(commands);
        parcommand.init();
        assertTrue("The commads didn't initialize", testCommand1.getHasInitalized() && testCommand2.getHasInitalized());
    }

    @Test
    public void testExecution() {
        TestCommand testCommand1 = new TestCommand();
        TestCommand testCommand2 = new TestCommand();
        ParallelCommand parcommand = new ParallelCommand(testCommand1, testCommand2);

        parcommand.init();

        parcommand.execute();
        assertFalse(parcommand.isFinished());
        assertEquals(1, testCommand1.getTimes());
        assertEquals(1, testCommand2.getTimes());

        testCommand2.setIsFinished(true);
        parcommand.execute();
        assertFalse(parcommand.isFinished());
        assertEquals(2, testCommand1.getTimes());
        assertEquals(2, testCommand2.getTimes());

        testCommand1.setIsFinished(true);
        parcommand.execute();
        assertTrue(parcommand.isFinished());
        assertEquals(3, testCommand1.getTimes());
        assertEquals(2, testCommand2.getTimes());
        assertTrue("the second one did not end", testCommand2.getHasEnded());

        parcommand.execute();
        assertEquals(3, testCommand1.getTimes());
        assertEquals(2, testCommand2.getTimes());

        assertTrue("command 1 didn't end", testCommand1.getHasEnded());
        assertTrue("command 2 didn't end", testCommand2.getHasEnded());
        assertTrue("command 1 didn't finish", testCommand1.isFinished());
        assertTrue("command 2 didn't finish", testCommand2.isFinished());
    }

    @Test
	public void testEmpty() {
		 ParallelCommand parralellommand = new ParallelCommand();
		parralellommand.init();
		parralellommand.execute();
		assertTrue(parralellommand.isFinished());
		parralellommand.end();
	}
}
