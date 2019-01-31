# LoadMonitor
This is a simple Java-based OS load monitor for collecting system performance metrics while trouble shooting performance issues of an application. It monitors CPU utilization, disk I/O, network I/O, and memory usage in real-time wiht a 1 second sampling period. This is useful in understanding the resource consumption pattern of an application.


Run the program with the following command. Replace "eth0" with the name of your NIC (eg, ens5), and "xvda" with the name of your block device (eg, nvme0n1).

~~~
java LoadMonitor eth0 xvda
~~~

The following is the output format:

~~~
cpuPercentUser + "\t" + cpuPercentSystem + "\t" + cpuPercentIdle + "\t" + cpuPercentIoWait
	+ "\t" + deltaDiskReadBytes + "\t" + deltaDiskWriteBytes 
	+ "\t" + deltaNicRxBytes + "\t" + deltaNicTxBytes
	+ "\t" + memFree (KB) + "\t" + memAvailable (KB)
~~~

