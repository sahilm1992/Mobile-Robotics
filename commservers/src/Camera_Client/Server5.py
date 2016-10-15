import socket
import cv
import threading
import logging
from threading import Thread
global capture
global samecolorclient
lock=threading.Lock()
colorlist=[]
samecolorclient=[]
capture = cv.CaptureFromCAM(0)

logging.basicConfig(level=logging.DEBUG,format='(%(threadName)-10s) %(message)s',)
        
class CreateThread(Thread):
    def __init__(self,threadName,i,color):
        self.threadName=threadName
        self.i=i
        self.color=color
        Thread.__init__(self)

    def run(self):
        logging.debug(' starting run ')
        global samecolorclient
        global capture
        mydata=threading.local()
        mydata.window1=str(self.threadName)+" Color Detection"
        mydata.window2=str(self.threadName)+" Threshold"
        cv.NamedWindow(mydata.window1,0)
        cv.NamedWindow(mydata.window2,0)
        #logging.debug('my parameters are %s %s %s',str(self.threadName),str(self.i),str(self.color))
        while True:
            mydata.color_image = cv.QueryFrame(capture)
            mydata.color_image = cv.GetSubRect(mydata.color_image,(70,70,450,390))
            cv.Flip(mydata.color_image,mydata.color_image,1)
            cv.Smooth(mydata.color_image, mydata.color_image, cv.CV_GAUSSIAN, 3, 0)
            logging.debug(' Starting getthresholdedimg ')
            mydata.imghsv=cv.CreateImage(cv.GetSize(mydata.color_image),8,3)
            cv.CvtColor(mydata.color_image,mydata.imghsv,cv.CV_BGR2HSV)	# Convert image from RGB to HSV
            mydata.imgnew=cv.CreateImage(cv.GetSize(mydata.color_image),cv.IPL_DEPTH_8U,1)
            mydata.imgthreshold=cv.CreateImage(cv.GetSize(mydata.color_image),8,1)
            mydata.c=self.color[0]
            mydata.minc=(float(mydata.c[0]),float(mydata.c[1]),float(mydata.c[2]))
            mydata.c=self.color[1]
            mydata.maxc=(float(mydata.c[0]),float(mydata.c[1]),float(mydata.c[2]))
            cv.InRangeS(mydata.imghsv, cv.Scalar(*(mydata.minc)), cv.Scalar(*(mydata.maxc)), mydata.imgnew);
            cv.Add(mydata.imgnew,mydata.imgthreshold,mydata.imgthreshold)
            logging.debug(' Exiting getthreasholdedimg')
            logging.debug('function returned from thresholdedimg')
            cv.Erode(mydata.imgthreshold,mydata.imgthreshold,None,3)
            cv.Dilate(mydata.imgthreshold,mydata.imgthreshold,None,10)
            mydata.img2=cv.CloneImage(mydata.imgthreshold)
            mydata.storage = cv.CreateMemStorage(0)
            mydata.contour = cv.FindContours(mydata.imgthreshold, mydata.storage, cv.CV_RETR_CCOMP, cv.CV_CHAIN_APPROX_SIMPLE)
            mydata.points=[]
            logging.debug('Starting while contour')
            while mydata.contour:
                # Draw bounding rectangles
                mydata.bound_rect = cv.BoundingRect(list(mydata.contour))
                mydata.contour = mydata.contour.h_next()
                mydata.pt1 = (mydata.bound_rect[0], mydata.bound_rect[1])
                mydata.pt2 = (mydata.bound_rect[0] + mydata.bound_rect[2], mydata.bound_rect[1] + mydata.bound_rect[3])
                mydata.points.append(mydata.pt1)
                mydata.points.append(mydata.pt2)
                cv.Rectangle(mydata.color_image, mydata.pt1, mydata.pt2, cv.CV_RGB(255,0,0), 1)
                # Calculating centroids 
                mydata.centroidx=cv.Round((mydata.pt1[0]+mydata.pt2[0])/2)
                mydata.centroidy=cv.Round((mydata.pt1[1]+mydata.pt2[1])/2)
                mydata.centroid=[mydata.centroidx,mydata.centroidy]
                mydata.tmpclient=[]
                mydata.tmpclient=samecolorclient[self.i]
                lock.acquire()
                for mydata.j in range(len(mydata.tmpclient)):
                    mydata.client_socket=mydata.tmpclient[mydata.j]
                    logging.debug('before centroid send...')
                    mydata.client_socket.send(str(mydata.centroid))
                    logging.debug('byte sent to client')
                lock.release()
                del mydata.centroid[:]
            cv.ShowImage(mydata.window1,mydata.color_image)
            cv.ShowImage(mydata.window2,mydata.img2)
        
            if cv.WaitKey(33)==27:
                cv.DestroyWindow(mydata.window1)
                cv.DestroyWindow(mydata.window2)
                break
            logging.debug(' exiting run ')

def Color_callibration(capture):
    vals=[];bgr=[];mini=[255,255,255];maxi=[0,0,0]
    cv.NamedWindow("BGR",0)
    print 'Please Put Your color in the circular area.Press ESC to start callibration:'
    while 1:
        image = cv.QueryFrame(capture)
        cv.Flip(image,image,1)
        cv.Circle(image, (int(200) , int(300)), 20, cv.CV_RGB(255, 255, 255), 6)
        cv.ShowImage("BGR",image)
        c = cv.WaitKey(33)
        if c== 27:
            break  
    for i in range(0,10):
        image = cv.QueryFrame(capture)
        cv.Flip(image,image,1)
        cv.Smooth(image, image, cv.CV_GAUSSIAN, 3, 0)
        imagehsv=cv.CreateImage(cv.GetSize(image),8,3)
        cv.CvtColor(image,imagehsv,cv.CV_BGR2HSV)
        vals=cv.Get2D(imagehsv, 300, 200)
        print 'blue:',vals[0],'green:',vals[1],'red:',vals[2]
        for j in range(0,3):
            if(vals[j]<mini[j]):mini[j]=vals[j]
            if(vals[j]>maxi[j]):maxi[j]=vals[j]
        cv.Circle(image, (int(200) , int(300)), 20, cv.CV_RGB(255, 255, 255), 6)
        cv.ShowImage("BGR",image)
        c = cv.WaitKey(33)
        if c== 27:
            break
    mini[0]-=10;mini[1]-=20;mini[2]-=50
    maxi[0]+=10;maxi[1]+=20;maxi[2]+=50
    for i in range(0,3):
        if(mini[i]<0):
            mini[i]=0
        if(maxi[i]>255):
            maxi[i]=255
    cv.DestroyWindow("BGR")
    print mini
    print maxi
    bgr=(mini,maxi)
    return bgr

if __name__=="__main__":
    logging.debug(' starting main ')
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('', 6685))
    print "TCPServer Waiting for clients on port 6685"
    server_socket.listen(5)
    n = int(raw_input('How many colors :'))
    mylist = []
    for i in range(0,n):
        tmp = Color_callibration(capture)
        mylist.insert(i,tmp)
    i=0
    while True:
        print i
        threadName = "Thread-"+str(i)
        print threadName, ' is to be started for the next client request '
        client_socket, address = server_socket.accept()
        print address," Want to get connected... "
        client_socket.send(str(mylist))
        a = client_socket.recv(512)
        print 'Got the color'
        choice = int(a)
        mycolor = []
        mycolor = mylist[choice]
        colormin = mycolor[0]
        colormax = mycolor[1]
        incolormin = (float(colormin[0]), float(colormin[1]), float(colormin[2]))
        incolormax = (float(colormax[0]), float(colormax[1]), float(colormax[2]))
        color=(incolormin,incolormax)
        print color
        flag=0
        try:
            for j in range(i):
                tmp=colorlist[j]
                ttmp1=tmp[0]
                ttmp2=tmp[1]
                tmpcolormin = (float(ttmp1[0]),float(ttmp1[1]), float(ttmp1[2]))
                tmpcolormax = (float(ttmp2[0]),float(ttmp2[1]), float(ttmp2[2]))
                if(tmpcolormin[0]==incolormin[0] and tmpcolormin[1]==incolormin[1] and tmpcolormin[2]==incolormin[2]):
                    if(tmpcolormax[0]==incolormax[0] and tmpcolormax[1]==incolormax[1] and tmpcolormax[2]==incolormax[2]):
                        samecolorclient[j].append(client_socket)
                        flag=1
            if (flag==0):
                colorlist.append(color)
                templist=[]
                templist.append(client_socket)
                print templist
                samecolorclient.insert(i,templist)
                print samecolorclient
                mythread=CreateThread( threadName,i,color )
                mythread.start()
                i=i+1
            print 'Connection Established'
        except (KeyboardInterrupt, SystemExit):
            cleanup_stop_thread();
            sys.exit()
    logging.debug(' exiting main ')
