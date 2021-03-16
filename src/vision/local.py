import cv2

from pi import process

if __name__ == '__main__':
    cam = cv2.VideoCapture(0)
    while True:
        ret_val, img = cam.read()
        img = cv2.flip(img, 1)
        img, values = process(img)
        cv2.imshow('my webcam', img)
        print(values)

        if cv2.waitKey(1) == 27: 
            break  # esc to quit
    cv2.destroyAllWindows()
