#!/usr/bin/env python

import socket
import struct
from helpers import quat2rot, rotationMatrixToEulerAngles
import numpy as np
import matplotlib.pyplot as plt


# Plot data
lines = []
axes = []
# Allocate data to plot
x = np.linspace(0, 6*np.pi, 100)
y = np.sin(x)

plt.ion()
fig = plt.figure()
ax = fig.add_subplot(311)
line1, = ax.plot(x, y, 'r-')
line2, = ax.plot(x, y, 'g-')
line3, = ax.plot(x, y, 'b-')
lines.append(line1)
lines.append(line2)
lines.append(line3)
axes.append(ax)

ax = fig.add_subplot(312)
line1, = ax.plot(x, y, 'r-')
line2, = ax.plot(x, y, 'g-')
line3, = ax.plot(x, y, 'b-')
lines.append(line1)
lines.append(line2)
lines.append(line3)
axes.append(ax)

ax = fig.add_subplot(313)
line1, = ax.plot(x, y, 'r-')
line2, = ax.plot(x, y, 'g-')
line3, = ax.plot(x, y, 'b-')
lines.append(line1)
lines.append(line2)
lines.append(line3)
axes.append(ax)

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
X = np.zeros((9,N), dtype=np.float32)
t = np.zeros(N, dtype=np.float32)
print( 'Connection address:', addr)
while 1:
	try:
		data = conn.recv(BUFFER_SIZE)
		if not data: continue#continue#break

		data = struct.unpack('>'+str(DATA_SIZE)+'f', data[0:4*DATA_SIZE])
		print('Received data: ', idx, end = '  ')
		for i in range(DATA_SIZE):
			print("{0:.2f}  ".format(data[i]), end='')
		# Get quaternions
		q[0] = data[8]
		q[1] = data[5]
		q[2] = data[6]
		q[3] = data[7]
		# Convert quaternions to RPY angles
		rpy = rotationMatrixToEulerAngles(quat2rot(q))
		rpy *= 180/np.pi
		print(', RPY = ', rpy.tolist())

		dt = 1./30#data[12]*NS2S
		X[0:3, idx] = data[9:12]
		### Integrate acc
		if idx == 1:
			t[idx] = t[idx-1] + dt
			X[3:6, idx] = X[0:3,idx-1]*dt
		elif idx>1:			
			t[idx] = t[idx-1] + dt
			X[3:6, idx] = X[3:6, idx-1] + X[0:3,idx-1]*dt
			X[6:9, idx] = X[6:9, idx-1] + X[3:6,idx-1]*dt
		idx += 1
		
		if idx >= N:
			X = np.concatenate((X,t.reshape(1,-1)), axis = 0)
			np.savetxt('data.txt', X.T)
		### Plot data ###
		for i in range(9):
			lines[i].set_xdata(t[0:idx])
			lines[i].set_ydata(X[i, 0:idx])
		for i in range(3):
			axes[i].relim()
			axes[i].autoscale()
		fig.canvas.draw()
		fig.canvas.flush_events()
		### End ###	
		# Forward data
		buf = struct.pack('<'+str(DATA_SIZE)+'f', *data)
		#s2.send(buf)
	except:
		break
	#conn.send(data)  # echo
conn.close()
