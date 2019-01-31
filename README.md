# LoadMonitor
Simple Java Load Monitor for System Performance Analysis


Run the program with the following command. Replace "eth0" with the name of your NIC (eg, ens5), and "xvda" with the name of your block device (eg, nvme0n1).

~~~
java LoadMonitor eth0 xvda
~~~

The following is the output format:

~~~
cpuPercentUser + "\t" + cpuPercentSystem + "\t" + cpuPercentIdle + "\t" + cpuPercentIoWait
	+ "\t" + deltaDiskReadBytes + "\t" + deltaDiskWriteBytes 
	+ "\t" + deltaNicRxBytes + "\t" + deltaNicTxBytes
	+ "\t" + memFree + "\t" + memAvailable
~~~

The sampling period is 1 second.
