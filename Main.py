#coding=utf-8
#*******************************
#author:wq
#this is main function,
#use curl and ping
#to deal different methods
#*******************************

import sys
import os
import csv
import random,time
import Tool,Log

VpCluster_vp={}
Vp_ip={}

class User(object):
    def __init__(self):
        #用户配置文件，对应VPC和url
        self.file_url = os.path.join(os.getcwd(), 'user\url.txt')
        self.file_config = os.path.join(os.getcwd(), 'user\configure.txt')
        #循环次数，为了以后取中值
        self.n = 2
        self.log = Log.MyLog()    
        
    def user_method(self, vpn, ip, ch):
        #ch = 0:get all url delay;one by one
        #ch = 1:repeat some url many times ;interval 1 minutes
        #ping
        self.log.main('tag_vpn', vpn + ' ' + ip)
        try: 
            #read url
            while True:
                result_ping = Tool.Ping(ip, self.n).run(vpn + '+' + ip)
                str_result_ping = result_ping.replace('\r\n', ' ')
                self.log.main('tag_ping', str_result_ping)
                with open(self.file_url) as fu:
                    for line_u in fu.readlines():
                        if line_u != '\n':#防止读到空行，则继续
                            print line_u
                            self.log.main('tag_url', line_u)
                            #curl
                            result_curl = Tool.Curl(line_u.strip('\n'), self.n + 1).run()
                            for i in result_curl:
                                self.log.main('tag_curl', i)
                                #self.output.main()
                        else:
                            #assert(1 != 1)
                            continue
                    #用户另一种方法，间隔一分钟，目前还没用上这个功能
                    if 0 == ch:
                        print 'here 0 break'
                        break
                    if 1 == ch:
                        time_record = 0
                        print 'here 1 break'
                        while (time_record < 1):
                            #5 minute
                            #time.sleep(300)
                            time.sleep(0)
                            time_record += 1
        except:
            raise ValueError
        return 0

   
    
class System(object):
    #读取系统和用户配置文件，给出user选择
    def read_sys_configure(self):
        #@param VP.CSV: 
        #@return: choice to user
        print 'System Initalizing...'
        filename = os.path.join(os.getcwd(), 'VP.csv')
        print filename
        reader = csv.reader(file(filename, 'rb'))
        record_vpc = []
        record_vp = []
        record_ip = []
        for line in reader:
            #print line, len(line)
            if 'VPC' in line:
                continue
            if line[0] != '':#vpc
                key_vpc = line[0]
                record_vpc.append(key_vpc)
                VpCluster_vp[key_vpc] = []
            if  line[1] != '':#vp
                key_vp = line[1]
                record_vp.append(key_vp)
                VpCluster_vp[record_vpc[-1]].append(line[1]) 
                Vp_ip[key_vp] = []    
            #if [line[2],line[3]] not in Vp_ip[record_vp[-1]]:
            #    Vp_ip[record_vp[-1]].append([line[2],line[3]])
            if line[2] != '':#ip
                key_ip = line[2]
                record_ip.append(key_ip)
                Vp_ip[record_vp[-1]].append(line[2])           
            #assert(len(line)==3)
        print len(VpCluster_vp), 'VPC read...'
        #print len(record_vp)
        #print len(record_vpc)
        #print len(record_ip)
        print len(Vp_ip), 'VP read...'
        #print VpCluster_vp
        #print Vp_ip
        #for test 
        '''total = 0
        for i in VpCluster_vp:
            #print i.decode('gbk')
            for j in VpCluster_vp[i]:
                print '   ', j.decode('gbk')
            total += len(VpCluster_vp[i])
        print 'total:', total
        total = 0
        #print len(Vp_ip)
        for j in Vp_ip:
            print j.decode('gbk'), Vp_ip[j]
            #print len(Vp_ip[j])
            #for k in Vp_ip[j]:
            #    print '   ', len(Vp_ip[j])
            total += len(Vp_ip[j])
        print total'''
        return 0
    
    def choose_to_user(self, vpc):
        #return  a random vpn to user 2015/8/17
        vps = VpCluster_vp[vpc]
        num = random.randint(1, len(vps))
        vp = vps[num-1]
        info = Vp_ip[vp]
        num = random.randint(1, len(info))
        ip = info[num-1]
        #print vpn
        #vpn_r = [vpn[0].encode('gbk'), vpn[1]]
        return vp, ip
    
    def loop_delete_bad_vpc(self, vpc, user, choice):
        bad_vpc = 0
        while True:
            vpn = self.choose_to_user(vpc)
            bad_vpc += 1
            print '\nConnect to vpn:', vpn[0].decode('gbk') + ' ' + vpn[1]
            print 'Ready?'#yes or no
            ready = raw_input().lower()
            #VPC只有一个坏的vp
            #print vpc
            #print len(VpCluster_vp[vpc])
            if len(VpCluster_vp[vpc]) == 1 and ready != 'yes':
                #vp = VpCluster_vp[vpc]
                #print vp[0]
                #print 
                #if len(Vp_ip[vp[0]]) == 1:
                    flag = 1
                    break
            if ready == 'yes':
                flag = 0  
                Tool.Output.current_tag = ''
                try:
                    Tool.Output.main('t', 0)#记录时间
                    user.user_method(vpn[0], vpn[1], choice)#8/17 ??
                    Tool.Output.main('t', 1)#记录时间
                except:
                    print 'can not ping vpn server'
                    #new 
                    return self.loop_delete_bad_vpc(vpc, user, choice)
                break
            if ready == 'all no':
                flag =- 1
                break
            if bad_vpc >= 8:
                #five vpn bad
                flag =- 1 
                break
        return flag
    
    def do_user_config(self, choice):
        #add two functions 2015/8/17
        #first:can skip bad vp; second:can skip bad vpc
        user = User()
        with open(user.file_config) as fc:
            for line in fc.readlines():
                #Tool.Output.main('t',0)#记录时间
                vpc = line.strip('\n')
                print vpc.decode('gbk')
                Tool.Output.main(vpc)#记录vpc
                #print vpc.decode('gbk')
                #vpns = []#record random vpn to ensure not repeat 2015/8/17
                tag=self.loop_delete_bad_vpc(vpc,user,choice)  
                if tag == 1 or tag == -1:#skip bad vpc 2015/8/17
                    continue
                
                        
                        

def main():
    #main 入口
	#path = os.path.dirname(sys.argv[0])
	#os.chdir(path)
    syst = System()
    syst.read_sys_configure()
    syst.do_user_config(0)
    #print 'Enter your method: '
    print 'END!!!'
    time.sleep(5)
    sys.exit()
    return 0

if __name__ == '__main__':
    main()