import numpy as np
import matplotlib.pyplot as plt

X = np.loadtxt('data.txt', dtype=np.float32)

fig = plt.figure()
for i in range(3):
	ax = fig.add_subplot(3,1,i+1)
	ax.plot(X[:,9],X[:,3*i+0], 'r-')
	ax.plot(X[:,9],X[:,3*i+1], 'g-')
	ax.plot(X[:,9],X[:,3*i+2], 'b-')

plt.show()


