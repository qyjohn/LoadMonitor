/**
 *
 * A simple utility to record timestamp and system load.
 *
 */
 
import java.lang.management.*;
import javax.management.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class LoadMonitor extends Thread
{	
	String node_ip, test_id;
	Connection db_connection;
	Properties prop = new Properties();
	String nicRxPath, nicTxPath, diskPath, cpuPath, memPath;
	long cpuUser, cpuSystem, cpuIdle, cpuIoWait, cpuTotal;
	float cpuPercentUser, cpuPercentSystem, cpuPercentIdle, cpuPercentIoWait;
	long diskReadIops, diskWriteIops, deltaDiskReadIops, deltaDiskWriteIops, diskReadSectors, diskWriteSectors, deltaDiskReadBytes, deltaDiskWriteBytes;
	long nicRxBytes, nicTxBytes, deltaNicRxBytes, deltaNicTxBytes;
	long memTotal, memFree, memAvailable;
	
	public LoadMonitor(String nic, String disk)
	{
		// Path to various information sources
		nicRxPath = "/sys/class/net/" + nic + "/statistics/rx_bytes";
		nicTxPath = "/sys/class/net/" + nic + "/statistics/tx_bytes";
		diskPath  = "/sys/class/block/" + disk + "/stat"; 
		cpuPath   = "/proc/stat";
		memPath   = "/proc/meminfo";
		
		// Initialize all the parameters
		getUsage();
	}
	
	public void getUsage()
	{
		try
		{
			// Get CPU usage
			String[] cpuInfo = readOneLine(cpuPath).split("\\s+");
			long newCpuUser   = Long.parseLong(cpuInfo[1]);
			long newCpuSystem = Long.parseLong(cpuInfo[3]);
			long newCpuIdle   = Long.parseLong(cpuInfo[4]);
			long newCpuIoWait = Long.parseLong(cpuInfo[5]);	// this is part of cpuIdle
			long newCpuTotal    = cpuUser + cpuSystem + cpuIdle;
			float deltaCpuTotal  = newCpuTotal  - cpuTotal;
			long deltaCpuUser   = newCpuUser   - cpuUser;
			long deltaCpuSystem = newCpuSystem - cpuSystem;
			long deltaCpuIdle   = newCpuIdle   - cpuIdle;
			long deltaCpuIoWait = newCpuIoWait - cpuIoWait;
			cpuTotal  = newCpuTotal;
			cpuUser   = newCpuUser;
			cpuSystem = newCpuSystem;
			cpuIdle   = newCpuIdle;
			cpuIoWait = newCpuIoWait;
			cpuPercentUser   = 100 * (deltaCpuUser   / deltaCpuTotal);
			cpuPercentSystem = 100 * (deltaCpuSystem / deltaCpuTotal);
			cpuPercentIdle   = 100 * (deltaCpuIdle   / deltaCpuTotal);
			cpuPercentIoWait = 100 * (deltaCpuIoWait / deltaCpuTotal);

			// Get MEM usage
			BufferedReader br = new BufferedReader(new FileReader(memPath));
			String line;
			int pos;
			line = br.readLine();	// MemTotal
                        pos = line.indexOf(" ");
                        line = line.substring(pos).trim();
                        pos = line.indexOf(" ");
                        line = line.substring(0, pos).trim();   
                        memTotal = Long.parseLong(line);         
			line = br.readLine();  // MemFree
			pos = line.indexOf(" ");
			line = line.substring(pos).trim();
			pos = line.indexOf(" ");
			line = line.substring(0, pos).trim();	
			memFree = Long.parseLong(line);		
			line = br.readLine();  // MemAvailable
			pos = line.indexOf(" ");
			line = line.substring(pos).trim();
			pos = line.indexOf(" ");
			line = line.substring(0, pos).trim();	
			memAvailable = Long.parseLong(line);		
			br.close();
			
			// Get disk I/O
			String[] diskInfo = readOneLine(diskPath).trim().split("\\s+");
                        long newDiskReadIops  = Long.parseLong(diskInfo[0]);
                        long newDiskWriteIops = Long.parseLong(diskInfo[4]);
			long newDiskReadSectors  = Long.parseLong(diskInfo[2]);
			long newDiskWriteSectors = Long.parseLong(diskInfo[6]);
			deltaDiskReadIops = newDiskReadIops - diskReadIops;
			diskReadIops = newDiskReadIops;
			deltaDiskWriteIops = newDiskWriteIops - diskWriteIops;
			diskWriteIops = newDiskWriteIops;
			deltaDiskReadBytes    = 512 * (newDiskReadSectors - diskReadSectors);
			diskReadSectors  = newDiskReadSectors;
			deltaDiskWriteBytes   = 512 * (newDiskWriteSectors - diskWriteSectors);
			diskWriteSectors = newDiskWriteSectors;

			// Get network I/O
			long newNicRxBytes = Long.parseLong(readOneLine(nicRxPath).trim());
			long newNicTxBytes = Long.parseLong(readOneLine(nicTxPath).trim());
			deltaNicRxBytes = newNicRxBytes - nicRxBytes;
			nicRxBytes = newNicRxBytes;
			deltaNicTxBytes = newNicTxBytes - nicTxBytes;
			nicTxBytes = newNicTxBytes;
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void print()
	{
		System.out.println(cpuPercentUser + "\t" + cpuPercentSystem + "\t" + cpuPercentIdle + "\t" + cpuPercentIoWait
			+ "\t" + deltaDiskReadIops + "\t" + deltaDiskReadBytes + "\t" + deltaDiskWriteIops + "\t" + deltaDiskWriteBytes
			+ "\t" + deltaNicRxBytes + "\t" + deltaNicTxBytes
			+ "\t" + memTotal + "\t" + memFree + "\t" + memAvailable);
	}
	
	public String readOneLine(String file)
	{
		try
		{
			// Read one line from file
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			reader.close();
			return line;
		} catch (Exception e)
		{
			return null;
		}
	}
	

	
	public void run()
	{
		while (true)
		{
			try
			{
				getUsage();
				print();
				sleep(1000);
			} catch (Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)
	{
		LoadMonitor lm = new LoadMonitor(args[0], args[1]);
		lm.start();
	}
	
}

