#!/usr/bin/env python

import socket
import struct

TCP_IP = '0.0.0.0'
TCP_PORT = 31006
SPORT = 31007
BUFFER_SIZE = 512  # Normally 1024, but we want fast response

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((TCP_IP, TCP_PORT))
s.listen(1)

conn, addr = s.accept()
#s2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#s2.connect(('127.0.0.1', SPORT))

print 'Connection address:', addr
while 1:
	try:
		data = conn.recv(BUFFER_SIZE)
		if not data: continue#continue#break
#		print "data len = ", len(data)
		## Uncomment the following to print floats
		f4 = struct.unpack('>5f', data[0:20])
		print "received data:", f4[0], " ", f4[1], " ", f4[2], " ", f4[3]," ", f4[4]
		buf = struct.pack('<5f', *f4)#[0],f4[1],f4[2])
		#s2.send(buf)
	except:
		break
	#conn.send(data)  # echo
conn.close()
