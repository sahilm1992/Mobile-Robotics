import socket
import cv
import string
import random
import re

def window_nameGenerator(size=6,chars=string.ascii_uppercase + string.digits):
    return ''.join(random.choice(chars) for x in range(size))

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect(("127.0.0.1", 6685))
print 'You have option for detecting only one color'
roboName = raw_input('Enter robo name : ')
print 'Enter color value for Your choice   '


a = client_socket.recv(512)
l=re.findall(r'[-+]?\d*\.\d+|\d+',a)
for i in range(0,len(l),6):
    print i/6,'th color','lowerbound:',l[i],',',l[i+1],',',l[i+2]
    print i/6,'th color','upperbound:',l[i+3],',',l[i+4],',',l[i+5]
a=raw_input('Enter your choice : ')
client_socket.send(str(a))
print 'min and max threshold colors for my choice is sent'
print ' Now am waiting for color detection'
name=window_nameGenerator()
cv.NamedWindow(name,0)
dirPath="C:\\Users\\sahil\\Documents\\robotics\\commservers\\Files\\camera_Pos\\"
while True:
    img = cv.LoadImage('./Arena.jpg')
    a = client_socket.recv(512)
    op_li= a.split(']')
    op_str = op_li[0].replace('[','')
    op_str =op_str.replace(' ','')
    f=open(dirPath+roboName +'.txt','w')

    newstr = a.replace("[", "")
    nnewstr = newstr.replace("]",",") #There was a bug here in parsing..corrected by Tushar 3/3/2016
    #print nnewstr
    mid=nnewstr.split(',')
    #print mid
    centroid=(float(mid[0]), float(mid[1]))
    #print a	
    #print mid[1]
    print centroid
    f.write(str(centroid[0])+","+ str(centroid[1]) + '\n')
    f.close()
    cv.Circle(img, (int(centroid[0]) , int(centroid[1])), 20, cv.CV_RGB(255, 255, 255), 1)
    cv.Circle(img, (int(centroid[0]) , int(centroid[1])), 10, cv.CV_RGB(255, 100, 0), 5)
    #print ' you see ! '
    
    cv.ShowImage(name,img)
    if cv.WaitKey(33) == 'q':
        cv.DestroyWindow(name)
        break
f.close()