import socket
import time        #abh
from cv2 import cv
import cv2
import threading
import logging
import thread
from threading import Thread
import numpy as np
import math
global capture
global samecolorclient
#global centroidList   #abh
lock=threading.Lock()            #abh
lock2=threading.Lock()            #abh
lock3=threading.Lock()            #abh
lock4=threading.Lock()            #abh
centroidList = [[0,0],[0,0],[0,0],[0,0],[0,0]]  #abh
colorlist=[]
samecolorclient=[]
capture = cv.CaptureFromCAM(0)
mylist = []

logging.basicConfig(filename = 'logfile.txt',level=logging.DEBUG,format='(%(threadName)-10s) %(message)s',)
        
class CreateThread(Thread):
    def __init__(self,i,color,server_socket):
        self.i=i
        self.color=color
        self.server_socket=server_socket
        Thread.__init__(self)

    def run(self):
        logging.debug(' starting run ')
        global samecolorclient
        global capture
        global centroidList #abh
        global lock         #abh
        global lock2        #abh
        global lock3        #abh
        global lock4        #abh
        mydata=threading.local()
        #window1=" Color Detection"
        mydata.window2=str(self.name)+" Threshold"
        #cv.NamedWindow(window1,0)
        lock4.acquire()           #abh
        cv.NamedWindow(mydata.window2,0)
        lock4.release()           #abh
        mydata.centroidold = [0,0]
        mydata.flag=0
        mydata.roi=[100,22,390,390]
        #mydata.roi=[95,40,380,350]
        while True:
            lock2.acquire()             #abh
            lock4.acquire()             #abh
            mydata.color_image = cv.QueryFrame(capture)
            lock4.release()              #abh
            lock2.release()             #abh
            if (mydata.flag==0):
                lock4.acquire        #abh lock4.release        #abh
                mydata.color_image = cv.GetSubRect(mydata.color_image,(100,22,390,390))
                lock4.release        #abh
            else:
                lock4.acquire        #abh lock4.release        #abh
                mydata.color_image = cv.GetSubRect(mydata.color_image,(int(mydata.roi[0]),int(mydata.roi[1]),int(mydata.roi[2]),int(mydata.roi[3])))
                lock4.release        #abh
            lock4.acquire        #abh lock4.release        #abh
            cv.Flip(mydata.color_image,mydata.color_image,1)
            cv.Smooth(mydata.color_image, mydata.color_image, cv.CV_MEDIAN, 3, 0)
            #logging.debug(' Starting getthresholdedimg ')
            mydata.imghsv=cv.CreateImage(cv.GetSize(mydata.color_image),8,3)
            cv.CvtColor(mydata.color_image,mydata.imghsv,cv.CV_BGR2YCrCb)	# Convert image from RGB to HSV
            mydata.imgnew=cv.CreateImage(cv.GetSize(mydata.color_image),cv.IPL_DEPTH_8U,1)
            mydata.imgthreshold=cv.CreateImage(cv.GetSize(mydata.color_image),8,1)
            lock4.release        #abh
            mydata.c=self.color[0]
            mydata.minc=(float(mydata.c[0]),float(mydata.c[1]),float(mydata.c[2]))
            mydata.c=self.color[1]
            mydata.maxc=(float(mydata.c[0]),float(mydata.c[1]),float(mydata.c[2]))
            lock4.acquire        #abh lock4.release        #abh
            cv.InRangeS(mydata.imghsv, cv.Scalar(*(mydata.minc)), cv.Scalar(*(mydata.maxc)), mydata.imgnew)
            cv.Add(mydata.imgnew,mydata.imgthreshold,mydata.imgthreshold)
            #logging.debug(' Exiting getthreasholdedimg')
            #logging.debug('function returned from thresholdedimg')
            cv.Erode(mydata.imgthreshold,mydata.imgthreshold,None,1)
            cv.Dilate(mydata.imgthreshold,mydata.imgthreshold,None,4)
            mydata.img2=cv.CloneImage(mydata.imgthreshold)
            mydata.storage = cv.CreateMemStorage(0)
            mydata.contour = cv.FindContours(mydata.imgthreshold, mydata.storage, cv.CV_RETR_EXTERNAL, cv.CV_CHAIN_APPROX_SIMPLE)
            lock4.release        #abh
            mydata.points=[]
            #logging.debug('Starting while contour')
            while mydata.contour:
                # Draw bounding rectangles
                lock4.acquire        #abh lock4.release        #abh
                mydata.bound_rect = cv.BoundingRect(list(mydata.contour))
                lock4.release        #abh
                mydata.contour = mydata.contour.h_next()
                mydata.pt1 = (mydata.bound_rect[0], mydata.bound_rect[1])
                mydata.pt2 = (mydata.bound_rect[0] + mydata.bound_rect[2], mydata.bound_rect[1] + mydata.bound_rect[3])
                mydata.points.append(mydata.pt1)
                mydata.points.append(mydata.pt2)
                lock4.acquire        #abh lock4.release        #abh
                cv.Rectangle(mydata.color_image, mydata.pt1, mydata.pt2, cv.CV_RGB(mydata.maxc[0],mydata.maxc[1],mydata.maxc[2]), 1)
                lock4.release        #abh
                # Calculating centroids
                if(((mydata.bound_rect[2]) * (mydata.bound_rect[3]))<3500):
                    #logging.debug('Inside iffffffffffffffffffffffff')
                    lock4.acquire        #abh lock4.release        #abh
                    mydata.centroidx=cv.Round((mydata.pt1[0]+mydata.pt2[0])/2)
                    mydata.centroidy=cv.Round((mydata.pt1[1]+mydata.pt2[1])/2)
                    lock4.release        #abh
                    if(mydata.flag==1):
                        #logging.debug("inside flag1")
                        mydata.centroidx=mydata.roi[0]+mydata.centroidx
                        mydata.centroidy=mydata.roi[1]+mydata.centroidy
                    mydata.centroidnew=[mydata.centroidx,mydata.centroidy]
                    #logging.debug('mydataroi[0] '+str(mydata.roi[0]) + ';centroidx ' + str(mydata.centroidx))
                    #logging.debug('mydataroi[1] '+str(mydata.roi[1]) + ';centroidy ' + str(mydata.centroidy))
                    #print mydata.centroidx                                 #abh
                    #print mydata.centroidy                                 #abh
                    mydata.tmpclient=[]
                    lock3.acquire()                                       #abh
                    mydata.tmpclient=samecolorclient[self.i]
                    lock3.release()                                       #abh
                    mydata.distance = math.sqrt(math.pow((mydata.centroidnew[0]-mydata.centroidold[0]),2)+math.pow((mydata.centroidnew[1]-mydata.centroidold[1]),2))
                    #lock.acquire()                                         #abh                                                            #abh commented
                    for mydata.j in range(len(mydata.tmpclient)):
                        mydata.client_socket=mydata.tmpclient[mydata.j]
                        #logging.debug('before centroid send...')
                        if (mydata.distance >= 1.50):
                            print 'inside 1.50 '
                            
                            #self.server_socket.sendto(str(mydata.centroidnew),mydata.client_socket) #abh
                            lock.acquire()                                                           #abh
                            centroidList[colorlist.index(self.color)]= mydata.centroidnew          #abh
                            del mydata.centroidold[:]
                            #logging.debug(str(centroidList))
                            self.server_socket.sendto(str(centroidList),mydata.client_socket)         #abh
                            lock.release()                                                            #abh
                            #logging.debug ('updating done.')                                                 #abh
                            #print centroidList                                                       #abh
                            mydata.centroidold = mydata.centroidnew[:]
                        else:
                            #self.server_socket.sendto(str(mydata.centroidold),mydata.client_socket) #abh
                            lock.acquire()                                                           #abh
                            centroidList[colorlist.index(self.color)] = mydata.centroidold           #abh
                            #logging.debug(str(centroidList))
                            self.server_socket.sendto(str(centroidList),mydata.client_socket)         #abh
                            lock.release()                                                           #abh
                            #logging.debug ('updating done2.')                                                  #abh
                            #print centroidList                                                       #abh
                    #    logging.debug('byte sent to client')
                    #lock.release()                                         #abh
                    mydata.roi[0]=mydata.centroidx-50
                    mydata.roi[1]=mydata.centroidy-50
                    if(mydata.roi[0]<95):
                        mydata.roi[0]=95
                    if(mydata.roi[1]<40):
                        mydata.roi[1]=40
                    mydata.roi[2]=100
                    mydata.roi[3]=100
                    if((mydata.roi[0]+mydata.roi[2])>475):
                        mydata.roi[0]=mydata.roi[0]-((mydata.roi[0]+mydata.roi[2])-475)
                    if((mydata.roi[1]+mydata.roi[3])>390):
                        mydata.roi[1]=mydata.roi[1]-((mydata.roi[1]+mydata.roi[3])-390)
                    #del mydata.centroidnew[:]
                    mydata.flag=1
            if mydata.contour is None:
                mydata.flag=0;
            #cv.ShowImage(window1,mydata.color_image)
            lock4.acquire        #abh lock4.release        #abh
            cv.ShowImage(mydata.window2,mydata.img2)
            lock4.release        #abh
        
            if cv.WaitKey(33)==27:                  #here it was 33 instead of 10
                #cv.DestroyWindow(mydata.window1)
                #cv.DestroyWindow(mydata.window2)
                break
            #logging.debug(' exiting run ')
            

def Color_callibration(capture):
    vals=[];bgr=[];mini=[255,255,255];maxi=[0,0,0]
    cv.NamedWindow("BGR",0)
    print 'Please Put Your color in the circular area.Press ESC to start Callibration:'
    while 1:
        image = cv.QueryFrame(capture)
        cv.Flip(image,image,1)
        cv.Circle(image, (int(200) , int(300)), 10, cv.CV_RGB(255, 255, 255), 4)
        cv.ShowImage("BGR",image)
        c = cv.WaitKey(33)
        if c== 27:
            break
    print 'Starting Callibration...Analyzing the Object...'
    for i in range(0,100):
        image = cv.QueryFrame(capture)
        cv.Flip(image,image,1)
        cv.Smooth(image, image, cv.CV_MEDIAN, 3, 0)
        imagehsv=cv.CreateImage(cv.GetSize(image),8,3)
        cv.CvtColor(image,imagehsv,cv.CV_BGR2YCrCb)
        vals=cv.Get2D(imagehsv, 300, 200)
        font = cv.InitFont(cv.CV_FONT_HERSHEY_SIMPLEX, 0.5, 1, 0, 2, 8)
        cv.PutText(image,"  "+str(vals[0])+","+str(vals[1])+","+str(vals[2]), (200,300),font, (55,25,255))
        for j in range(0,3):
            if(vals[j]<mini[j]):mini[j]=vals[j]
            if(vals[j]>maxi[j]):maxi[j]=vals[j]
        cv.Circle(image, (int(200) , int(300)), 10, cv.CV_RGB(255, 255, 255), 4)
        cv.ShowImage("BGR",image)
        c = cv.WaitKey(33)
        if c== 27:
            break
    print 'Analyzation Completed'
    mini[0]-=35;mini[1]-=15;mini[2]-=15
    maxi[0]+=35;maxi[1]+=15;maxi[2]+=15
    for i in range(0,3):
        if(mini[i]<0):
            mini[i]=0
        if(maxi[i]>255):
            maxi[i]=255
    cv.DestroyWindow("BGR")
    bgr=(mini,maxi)
    return bgr

#def centroidSender (server_socket):                                  #abh
#    global colorlist                                                 #abh
#    global samecolorclient                                           #abh
#    global centroidList                                              #abh
#    while True:                                                      #abh
#        for index in range(len(colorlist)):                          #abh
#            for client in samecolorclient[index]:                    #abh
#                loggging.debug (str(centroidList))                              #abh
#                server_socket.sendto(str(centroidList),client)       #abh

"""def edge_detect(capture):
    for i in range(0,10):
        frame = cv.QueryFrame(capture)
    frame = cv.GetSubRect(frame,(95,40,380,350))
    frame_gray = cv.CreateImage(cv.GetSize(frame), 8, 1)
    frame_edges = cv.CreateImage(cv.GetSize(frame), 8, 1)
    cv.Smooth(frame, frame, cv.CV_GAUSSIAN, 3, 0)
    cv.CvtColor(frame, frame_gray, cv.CV_RGB2GRAY)
    cv.Canny(frame_gray, frame_edges, 120, 150)
    cv.SaveImage('edges.jpg',frame_edges)
    return frame_edges
    
def centroid_detect(frame_edges):
    storage = cv.CreateMemStorage(0)
    contour = cv.FindContours(frame_edges, storage, cv.CV_RETR_EXTERNAL, cv.CV_CHAIN_APPROX_SIMPLE)
    points=[]
    cs=[]
    while contour:
        bound_rect = cv.BoundingRect(list(contour))
        contour = contour.h_next()
        pt1 = (bound_rect[0],bound_rect[1])
        pt2 = (bound_rect[0] + bound_rect[2], bound_rect[1] +bound_rect[3])
        points.append(pt1)
        points.append(pt2)
        cv.Rectangle(frame_edges, pt1,pt2, cv.CV_RGB(255,100,100), 1)
        final=cv.CloneImage(frame_edges)
        cv.SaveImage('boxes.jpg',frame_edges)
        if(((bound_rect[2]) * (bound_rect[3]))>100):
            centroidx=cv.Round((pt1[0]+pt2[0])/2)
            centroidy=cv.Round((pt1[1]+pt2[1])/2)
            centroid=(centroidx,centroidy)
            cs.append(centroid)
            print centroidx, centroidy
    for c in cs:
        cv.Circle(final, (int(c[0]) , int(c[1])), 10, cv.CV_RGB(255, 50, 50), 4)
        cv.SaveImage('boxescircle.jpg',final) 
    return cs
    
def Color_callibration(capture,c):
    vals=[];bgr=[];mini=[255,255,255];maxi=[0,0,0]
    cv.NamedWindow("BGR",0)
    print 'Starting Callibration...Analyzing the Object...'
    for i in range(0,100):
        image = cv.QueryFrame(capture)
        image = cv.GetSubRect(image,(95,40,380,350))
        cv.Smooth(image, image, cv.CV_MEDIAN, 3, 0)
        imagehsv=cv.CreateImage(cv.GetSize(image),8,3)
        cv.CvtColor(image,imagehsv,cv.CV_BGR2HSV)
        vals=cv.Get2D(imagehsv, int(c[1]), int(c[0]))
        font = cv.InitFont(cv.CV_FONT_HERSHEY_SCRIPT_SIMPLEX , 0.5, 0.7, 0, 2, 8)
        cv.PutText(image,"  "+str(vals[0])+","+str(vals[1])+","+str(vals[2]), (0,20),font, (55,25,255))
        for j in range(0,3):
            if(vals[j]<mini[j]):mini[j]=vals[j]
            if(vals[j]>maxi[j]):maxi[j]=vals[j]
        cv.Circle(image, (int(c[0]) , int(c[1])), 10, cv.CV_RGB(255, 255, 255), 4)
        cv.ShowImage("BGR",image)
        k = cv.WaitKey(33)
        if k== 27:
            break
    print 'Analyzation Completed'
    mini[0]-=10;mini[1]-=20;mini[2]-=30
    maxi[0]+=10;maxi[1]+=20;maxi[2]+=30
    for i in range(0,3):
        if(mini[i]<0):
            mini[i]=0
        if(maxi[i]>255):
            maxi[i]=255
    cv.DestroyWindow("BGR")
    bgr=(mini,maxi)
    return bgr"""




if __name__=="__main__":
    logging.debug(' Jarvis at your Service... ')
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_socket.bind(('', 6685))
    print "UDPServer Waiting for clients on Port 6685"
    n = int(raw_input('How many colors :'))
    mylist = []
    for i in range(0,n):
        tmp = Color_callibration(capture)
        mylist.insert(i,tmp)
    i=0
    #thread.start_new_thread(centroidSender, (server_socket,))           #abh
    """logging.debug(' Jarvis at your Service... ')
    #winsound.PlaySound("jarvis_uploaded.wav", winsound.SND_FILENAME)
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_socket.bind(('', 6685))
    print "UDPServer Waiting for clients on Port 6685"
    edges = edge_detect(capture)
    cs = centroid_detect(edges)
    i=0
    for c in cs:
        print c
        tmp = Color_callibration(capture,c)
        mylist.insert(i,tmp)
        i=i+1
    i=0"""
    while True:
        threadName = "Thread-"+str(i)
        print threadName, ' is to be started for the next client request '
        hello,(addra,porta) = server_socket.recvfrom(512)
        server_socket.sendto(str(mylist),(addra,porta))
        a = server_socket.recv(512)
        print 'Got the color'
        choice = int(a)
        mycolor = []
        mycolor = mylist[choice]
        colormin = mycolor[0]
        colormax = mycolor[1]
        incolormin = (float(colormin[0]), float(colormin[1]), float(colormin[2]))
        incolormax = (float(colormax[0]), float(colormax[1]), float(colormax[2]))
        color=(incolormin,incolormax)
        flag=0
        time.sleep(3)                      #abh
        try:
            for j in range(i):
                tmp=colorlist[j]
                ttmp1=tmp[0]
                ttmp2=tmp[1]
                tmpcolormin = (float(ttmp1[0]),float(ttmp1[1]), float(ttmp1[2]))
                tmpcolormax = (float(ttmp2[0]),float(ttmp2[1]), float(ttmp2[2]))
                if(tmpcolormin[0]==incolormin[0] and tmpcolormin[1]==incolormin[1] and tmpcolormin[2]==incolormin[2]):
                    if(tmpcolormax[0]==incolormax[0] and tmpcolormax[1]==incolormax[1] and tmpcolormax[2]==incolormax[2]):
                        samecolorclient[j].append((addra,porta))
                        flag=1
            if (flag==0):
                colorlist.append(color)
                templist=[]
                templist.append((addra,porta))
                samecolorclient.insert(i,templist)
                mythread=CreateThread( i,color,server_socket )
                mythread.start()
                i=i+1
            print 'Connection Established'
        except (KeyboardInterrupt, SystemExit):
            cleanup_stop_thread();
            sys.exit()
    logging.debug(' exiting main ')
