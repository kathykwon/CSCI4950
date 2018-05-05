#!/usr/bin/python
from mininet.net import Mininet
from mininet.node import RemoteController, OVSSwitch, CPULimitedHost
from mininet.topo import LinearTopo
from mininet.link import TCULink
from mininet.cli import CLI
from mininet.log import setLogLevel
import time
import csv

def placeController():

	net = Mininet(topo=None, link=TCULink, build=False)


	print "*** Adding Controller ***"
	c0 = RemoteController('c0',ip='192.168.56.102', port=6633);
	net.addController(c0);

	#Linear Topology - Each host gets their own switch
	print "*** Adding Switches ***" 
	print "*** Adding Hosts ***"
	citylist = dict()
	with open('nodes.csv', 'r') as csvfile:
		rreader = csv.reader(csvfile)
		next(rreader)
		i = 0
		for row in rreader:
			if row[2] not in citylist:
				citylist[row[2]] = 's%d' % (i)
				switch = net.addSwitch('s%d' % (i), cls=OVSSwitch)
				
				host = net.addHost(row[2], type=CPULimitedHost, cpu=.5/30)
				net.addLink(switch, host,bw=100, delay='5ms')
				i = i+1
	#print citylist.keys()	

	print '\n*** INTERNET2 ***'
	#Create Links between switches
	with open('links.csv', 'r') as csvfile:
		rreader = csv.reader(csvfile)
		next(rreader)
		for row in rreader:
			net.addLink(citylist[row[1]],citylist[row[2]],bw=int(row[3]),delay='2ms') #Mbps -> Gbps

	 
	
	net.build()
	net.start()
	
	i = 0
	net.pingAll()
	#CLI (net )
	for city in citylist:
		avg_throughput = 0.0
		avg_jitter = 0.0
		avg_rtt = 0.0
		print ('*** '+city+' *** %d' % i)
		print ('Testing udp throughput with 3 sec, max bandwidth 160KByte UDP buffer size iperf test ')
		net.get(city).popen('iperf -us')
		host = net.get(city)
		for city2 in citylist:
			print ''
			s = ''
			
			if city != city2: 
				# use iperf to get Throughput and Jitter statisics
				s = net.get(city2).cmd('iperf -uc ' + host.IP() + ' -b 100M -t 3')
				s = s.splitlines()

				s = s[10].split(' ') #All the information we want is in the last row

				# use ping to get delays
				t = net.get(city2).cmd('ping -c 10 -i .25 ' + host.IP())
				t = t.splitlines()
				t = t[-1]
			

				print '%15s' % city2 + '\tTime: ' + s[5] + ' seconds\tLength: ' + s[8] + ' ' + s[9] +'\tThroughput: ' + s[11] + ' ' + s[12] + '\tJitter: ' + s[15] + ' ' + s[16] + '     ' + t
				avg_throughput = avg_throughput + float(s[11]) / (len(citylist) - 1)
				avg_jitter = avg_jitter + float(s[15]) / (len(citylist) - 1)
				avg_rtt = avg_rtt + float(t[30:35]) / (len(citylist) - 1)
		print ('Average Throughput: ' + str(avg_throughput) + ' Mbits/sec')
		print ('Average Jitter: ' + str(avg_jitter) +  ' ms')
		print ('Average RTT: ' + str(avg_rtt) + ' ms\n\n')
		i = i+1
if __name__ == "__main__":
	placeController()


