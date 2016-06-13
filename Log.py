#coding=utf-8
#@version: 2015/7/18
#write information to log file 
#including:time,info,some tag(differ content and tool)
import logging
import os

class MyLog(object):
    
    def __init__(self):
        logging.basicConfig(filename = os.path.join(os.getcwd(), 'source\log.txt'),
                            format = '%(asctime)s - %(levelname)s: %(message)s', level = logging.DEBUG)
     
    def main(self, tag, info):
        logging.info(tag + '\t' + info)
    

        
#log = MyLog()
#log.main('error', 'error')