import cv
img=cv.LoadImage("D:\Arena.jpg")
sub=cv.GetSubRect(img, (700,525,200,119))
cv.NamedWindow("result",1)
cv.ShowImage("result",sub)
if cv.WaitKey(33)==27:
                cv.DestroyWindow("result")
              #  cv.DestroyWindow(mydata.window2)
                break
            