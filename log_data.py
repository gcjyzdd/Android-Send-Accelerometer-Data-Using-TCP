#!/usr/bin/env python

import socket
import struct
from helpers import quat2rot, rotationMatrixToEulerAngles
import numpy as np
import matplotlib.pyplot as plt


# Prepare TCP server
TCP_IP = '0.0.0.0'
TCP_PORT = 31006
SPORT = 31007
BUFFER_SIZE = 13*4  # Normally 1024, but we want fast response

NS2S = 1.0 / 1e9;

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((TCP_IP, TCP_PORT))
s.listen(1)

conn, addr = s.accept()
#s2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#s2.connect(('127.0.0.1', SPORT))
DATA_SIZE = 13
q = np.zeros(4)


idx = 0
N = 256
X = np.zeros((3,N), dtype=np.float32)
t = np.zeros(N, dtype=np.float32)
print( 'Connection address:', addr)
while 1:
	try:
		data = conn.recv(BUFFER_SIZE)
		if not data: continue#continue#break

		data = struct.unpack('>'+str(DATA_SIZE)+'f', data[0:4*DATA_SIZE])
		print('Received data ', idx)
		
		#print('Received data: ', idx, end = '  ')
		#print(' '.join(data))
		#for i in range(DATA_SIZE):
		#	print("{0:.2f}  ".format(data[i]), end='')
		# Get quaternions
		q[0] = data[8]
		q[1] = data[5]
		q[2] = data[6]
		q[3] = data[7]
		
		# Convert quaternions to RPY angles
		#rpy = rotationMatrixToEulerAngles(quat2rot(q))
		#rpy *= 180/np.pi
		#print(', RPY = ', rpy.tolist())

		dt = data[12]
		X[0:3, idx] = data[9:12]
		if idx>0:
			t[idx] = dt

		idx += 1		
		if idx >= N:
			print('Save data')
			X = np.concatenate((X,t.reshape(1,-1)), axis = 0)
			np.savetxt('data2.txt', X.T)
			break

		
		# Forward data
		#buf = struct.pack('<'+str(DATA_SIZE)+'f', *data)
		#s2.send(buf)
	except:
		break
	#conn.send(data)  # echo
conn.close()
