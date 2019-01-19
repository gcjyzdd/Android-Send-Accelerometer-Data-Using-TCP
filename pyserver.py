#!/usr/bin/env python

import socket
import struct
from helpers import quat2rot, rotationMatrixToEulerAngles
import numpy as np


TCP_IP = '0.0.0.0'
TCP_PORT = 31006
SPORT = 31007
BUFFER_SIZE = 518  # Normally 1024, but we want fast response

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((TCP_IP, TCP_PORT))
s.listen(1)

conn, addr = s.accept()
#s2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#s2.connect(('127.0.0.1', SPORT))
DATA_SIZE = 9
q = np.zeros(4)

print( 'Connection address:', addr)
while 1:
	try:
		data = conn.recv(BUFFER_SIZE)
		if not data: continue#continue#break
#		print "data len = ", len(data)
		## Uncomment the following to print floats
		data = struct.unpack('>'+str(DATA_SIZE)+'f', data[0:4*DATA_SIZE])
		print('Received data: ', end = '')
		for i in range(DATA_SIZE):
			print("{0:.2f}  ".format(data[i]), end='')
			
		q[0] = data[8]
		q[1] = data[5]
		q[2] = data[6]
		q[3] = data[7]

		rpy = rotationMatrixToEulerAngles(quat2rot(q))
		rpy *= 180/np.pi
		print(', RPY = ', rpy.tolist())

		buf = struct.pack('<'+str(DATA_SIZE)+'f', *data)#[0],f4[1],f4[2])
		#s2.send(buf)
	except:
		break
	#conn.send(data)  # echo
conn.close()
