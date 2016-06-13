#coding=utf-8
#@version: 2015/7/18
#including :
#    tool_ping :ping module
#    tool_curl :curl module
#    tool_redirect :redirect module

import random,re
import os
import datetime
import subprocess as sp


class Shell(object):
    #调用subprocess 执行shell命令，结果输出到管道
    def __init__(self, cmd):
        self.cmd = cmd
        self.result = ''
    
    def execute_command(self):
        try:
            sub = sp.Popen(self.cmd, stdout = sp.PIPE, stderr = sp.PIPE)
            (stdoutput,erroutput) = sub.communicate()
            #print  stdoutput
            #print  erroutput
            return (stdoutput, erroutput) 
            #assert(1 != 1)
        except Exception, e:
            print 'ERROR %s:'% e 
            return self.execute_command(self)   


class Info(object):
    #得出中间结果,info_ping,info_curl,时间单位统一为毫秒,便于写入文件 
    def __init__(self):
        self.tag = ''
        self.ping = []
        self.time_server = []
        self.redirect = []
        self.server_ip = ''
        self.server_ping = []
    
    def get_static_dynamic(self, url):
        dynamic = ['club.jd.com', 'cart.jd.com', 'rate.tmall.com', 'cart.tmall.com', 'review.suning.com', 'cart.suning.com']
        flag = 'Static'
        ec_tag = 'Suning'
        ec=['tmall.', 'alicdn.', 'jd.', '360buyimg.', 'yihaodianimg.']
        for j in ec:
            if j in url:
                value = ec.index(j)
                if value < 2:
                    ec_tag = 'Tmall'
                elif value < 4:
                    ec_tag = 'Jd'
                else:
                    ec_tag = 'Yhd'
                break
        for i in dynamic:
            if i in url:
                flag = 'Dynamic' 
                break
        self.tag = ec_tag + '\t' + url + '\t' + flag
        return 0
    
  
    def get_ping_delay(self, content):
        #得到ping的delay值
        #print content.decode('gbk')
        p = re.findall('= (\d+)ms', content)
        #print p
        length = len(p)
        if length == 3 :
            delay_min = p[0]
            delay_max = p[1]
            delay_avg = p[2]
            print delay_min, delay_max, delay_avg
        else:
            #print 'ok'
            delay_min = '-1'
            delay_max = '-1'
            delay_avg = '-1'
        self.ping = [delay_min, delay_max, delay_avg]
    
    def get_curl_time_server(self, content):
        #time_namelookup,time_connect,time_pretransfer,time_starttransfer
        #time_redirect,time_total
        #speed_download,size_download,num_connects,http_code
        if ':http://' in content:
            p = re.split(':http://', content)
        if ':https://' in content:
            p = re.split(':https://', content)
        #print p
        print p[0]
        #print p[1]
        #get record time
        p1 = re.split(':', p[0])
        #print len(p1)
        assert(len(p1)==10)
        #get server ip
        p2 = re.findall(':(\d+\.\d+\.\d+\.\d+):', p[1])
        #s-->ms 单位统一为毫秒
        #record_time = []
        for i in range(6):
            t = float(p1[i]) * 1000
            #print t
            #record_time.append(t)
            self.time_server.append(t)
        #print record_time
        #单位Byte/ms
        speed = float(p1[6]) / 1000
        #print speed
        #print 'server ip:',p2[0]
        if len(p2) != 0:
            self.server_ip = p2[0]
        else:
            self.server_ip = '0.0.0.0'
        #ping server ip
        result_t = Ping(self.server_ip, 2).run()
        self.get_ping_delay(result_t)
        self.time_server.extend([speed, self.server_ip])
        print self.time_server
    
    def get_curl_redirect(self, content):
        #get ip and domain name
        dns = []
        ips = []
        p = re.findall('Trying (\d+\.\d+\.\d+\.\d+)', content)
        p1 = re.findall('connect\(\) to (.+) port', content)
        if p is not None:
            for i in p:
                ips.append(i)
        if p1 is not None:
            for j in p1:
                dns.append(j)
        #assert(len(p) == len(p1))
        print dns
        print ips
        #只有一个ip
        '''for i in ips:
            #只要有一个ip与server_ip不同，说明存在重定向
            if i != self.server_ip:
                print 'redirect'
                self.redirect = [dns,ips]
                break'''
        if ips[-1] != self.server_ip:
            print 'redirect'
            self.redirect = [dns, ips]
        return 0
    
    def main(self, *kwg):
        #计算出中值，记录到文件
        if len(kwg) == 3:
            #写入ping结果
            self.get_ping_delay(kwg[0])
            str_vp = kwg[1]
            #str_ping = self.ping[0] + '\t' + self.ping[1] + '\t' + self.ping[2]
            str_ping = self.ping[2]
            if str_ping == '-1':
                raise ValueError
            Output.main(str_vp, str_ping)
        else:
            #写入curl结果
            self.get_curl_time_server(kwg[0])
            self.get_curl_redirect(kwg[1])
            i=0
            str_time = ''
            str_ip = ''
            str_domain = ''
            str_ping = ''
            #写入时间信息
            while i < 7:
                str_time += (str(self.time_server[i]) + '\t')
                i+=1
            #写入redirect信息
            if self.redirect == []:
                str_ip = self.server_ip
            else:
                #write ip
                '''for j in self.redirect[1]:
                    str_ip += (j + '  ')'''
                str_ip = self.redirect[1][-1]
                #write domain
                for j in self.redirect[0]:
                    str_domain += (j + '  ')
            #写入ping server信息
            #str_ping = self.ping[0] + '\t' + self.ping[1] + '\t' + self.ping[2]
            str_ping = self.ping[2]
            if str_ping == '-1':
                raise ValueError
            #Output.main(self.tag, str_time + str_ip + str_domain, str_ping)
            Output.main(self.tag, str_time + str_ip, str_ping)
            self.time_server = []
            self.redirect = []
            
            
        return 0
          
class Output(object):
    #得出中间结果，写入文件，便于格式控制
    #static data
    current_tag = ''
    file = os.path.join(os.getcwd(), 'result\curl.txt')
    
    @staticmethod   
    def main(*kwg):
        #记录测量信息
        f = open(Output.file, 'a+')
        length = len(kwg)
        if length == 2:#main('t', 0) or main('t', 1)
            if kwg[1] == 0:#写入开始时间
                f.write('Begin: ' + str(datetime.datetime.now()) + '\n')
                return -1
            if kwg[1] == 1:#写入结束时间
                f.write('End: ' + str(datetime.datetime.now()) + '\n')
                f.write('\n')
                return 0
        #main(vpc)
        if length == 1:
            f.write(kwg[0] + '\n')
        #main(vp,ping)
        if length == 2:
            f.write('\t' + kwg[0] + '\t' + kwg[1] + '\n')
        #main(tag, info_curl, info_ping)
        if length == 3:
            tmp_tag = re.split('\t', kwg[0])
            print tmp_tag[0]#ec_tag
            #控制什么时候写ec_tag
            if Output.current_tag == '' or tmp_tag[0] != Output.current_tag:
                Output.current_tag = tmp_tag[0]
                f.write('\t' + '\t' + kwg[0] + '\t' + kwg[1] + '\t' + kwg[2] + '\t' + str(datetime.datetime.now()) + '\n')
            else:
                str_tag = tmp_tag[1] + '\t' + tmp_tag[2]
                f.write('\t' + '\t' + '\t' + str_tag + '\t' + kwg[1] + '\t' + kwg[2] + '\t' + str(datetime.datetime.now()) + '\n')
        return 1          
          
          
class Ping(Shell):
    def __init__(self, ip, n):
        self.n = n
        self.ip = ip
        Shell.__init__(self, "ping " + self.ip + " -n " + str(self.n))
        
    def run(self, *kwg):
        info = Info()
        result = self.execute_command()
        result_t = result[0] + result[1]
        result_p = result_t.replace('\r\n', ' ')
        if len(kwg) == 1:
            #vp = kwg[0]
            try:
                info.main(result_p, kwg[0], 0)
                return result_p
            except:
                raise ValueError
        return result_p
        
#result_t = Ping('202.38.64.56',5).run()
#result_p = result_t.replace('\r\n', ' ')
#print result_p


class Curl(Shell):
    def __init__(self, url, n):
        self.header_info = """ -H "Accept-Encoding: gzip, deflate, sdch" -H "Accept-Language: zh-CN,zh;q=0.8" -H "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36" -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"  -H "Connection: keep-alive" """
        self.cvs = """ --connect-timeout 5 -m 60 -L -v -s -o null.txt -w %{time_namelookup}:%{time_connect}:%{time_pretransfer}:%{time_starttransfer}:%{time_redirect}:%{time_total}:%{speed_download}:%{size_download}:%{num_connects}:%{http_code}:%{url_effective}:%{remote_ip}:%{remote_port}:%{local_ip}:%{local_port}"""
        self.url = url
        self.n = n
        self.cmd = ''
    
    def run(self):
        outputs = []
        info = Info()
        #print self.header_info
        #print self.cvs
        #print self.cmd
        #print self.url
        info.get_static_dynamic(self.url)
        for i in range(self.n):
            #add random for no cache
            if '?' not in self.url:
                url = self.url + '?ts=' + str(random.randint(100,100000))
            else :
                url = self.url + '&ts=' + str(random.randint(100, 100000))
            #print url
            self.cmd = "curl " + self.cvs + self.header_info + '"' + url + '" '
            #print self.cmd
            result = self.execute_command()
            str_result_0 = result[0].replace('\r\n', ' ')
            str_result_1 = result[1].replace('\r\n', ' ')
            #print '1',result[0]
            #print '2',result[1]
            try:
                info.main(result[0], result[1])
            except:
                raise ValueError
            outputs.append('num_' + str(i) + ' ' + str_result_0 + ' ' + str_result_1)
            #print output
        return outputs
#str_result = Curl('http://detail.tmall.com/item.htm?spm=a1z10.3-b.w4011-2652744000.358.qNeRNj&id=520632042912&rn=19d88f5cf42049a13f27bace852d89d9&abbucket=16&sku_properties=10537981:30189327',3).run()    
#print str_result

#Info.get_ping_delay(result_t)
#log = Log.MyLog()
#log.main('tag_ping', result_p)
#for i in str_result:
#    log.main('tag_curl', i)

