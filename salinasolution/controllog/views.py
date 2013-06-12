 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.views.decorators.csrf import csrf_exempt
from salinasolution.debug import debug
from salinasolution.feedback.models import Feedback
from salinasolution.controllog.models import Session
from salinasolution.userinfo.models import App
from django.core import serializers
from django.http import HttpResponse
import os
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution import pson, templetobj
import redis
import simplejson as json
import salinasolution.var as Var
import salinasolution.templetobj
from salinasolution.var import SYSTEM_SYSTEM_TYPE, SYSTEM_USER_TYPE
import redis
import salinasolution.ast as ast
from django.conf.locale import tr
from django.contrib.auth.decorators import login_required
import uuid
from django.template.loader import render_to_string

'''
controllog create 하는 연산
'''

#Redis Port num은 6379

r = redis.StrictRedis(host = 'localhost', port=Var.REDIS_PORT_NUM, db=0)
'''
####################################################################################
system feedback을 저장하는 부분
####################################################################################
'''

def send_device_key(request):
    value = str(uuid.uuid4())
    return HttpResponse(value)

#controllog를 저장하는 부분
@csrf_exempt
def save_system_feedback(request):
    
    print "start save_system_feedback"
    
    if request.method == 'POST' :
        
        dic = str(json.loads(request.raw_post_data))
        dic = ast.literal_eval(dic)
        dic = dic["system_feedback"]
        
        dic_key_list = dic.keys()
        print dic_key_list
        
        
        for key in dic_key_list:
                
                #Session인 경우 redis에 저장
                if key == 'Session':
                    session_list = dic[key]
                    #print "session list" + str(session_list)
                    for session in session_list:
                        #print str(session)
                        r.rpush(key, session)
                        app_id = session[Var.APP_ID]
                        print "app_id : " + app_id
                #DeviceInfo인 경우 redis에 저장    
                elif key == 'DeviceInfo':
                    #print "deviceinfo" + str(dic[key])
                    r.rpush(key, dic[key])
                
                if key == 'ScreenFlow':
                    #print "screenflow before"
                    #print "dic : " + str(dic[key])
                    #print "app_id : " + app_id
                    save_screen_flow(dic[key], app_id)
                    #print "screenflow after"
                
                #여긴 준영이랑 논의
                elif key == 'Event':
                    print "event"
                    save_trigger_event(dic[key], app_id)
                
        return "success"    


def save_trigger_event(event_list, app_id):
    
    
    print "event list : " + str(event_list)
    ex_screen_name = ''
    event_container = []
    
    for event_dic in reversed(event_list):
        
        cur_screen_name = event_dic['screen_name']
        print "cur_screen :" +  cur_screen_name
        event_name = event_dic['event_name']
        
        #처음 인경우
        if event_list.index(event_dic) == len(event_list) - 1 :
            ex_screen_name = cur_screen_name
       
       #같다면
        if ex_screen_name == cur_screen_name :
            event_container.append(event_name)
        
        #다르다면    
        else :
            screen_key = app_id + "_" + cur_screen_name
            print "screen_key : " + screen_key 
             
            #redis에 추가하는 부분
            for event in event_container:
                r.zincrby(screen_key, event, 1)
            event_container = []
            ex_screen_name = cur_screen_name
        
        
        #if i == len(reverse_event_list) :
        


def save_screen_flow(screen_list, app_id):
    
    for screen in reversed(screen_list):
            #마지막 값일 경우에는 break
            if 0 == screen_list.index(screen):
                #print "save_screen_flow : break"
                break
            #마지막 값이 아닐 경우에는 app_id를 사용해서
            screen_key = app_id + "_" + screen 
            #print "screek_key : " + screen_key
            next_index = screen_list.index(screen) - 1
            input_screen_name = screen_list[next_index]
            #실제로 screen을 삽입하는 부분
            #screen 존재시
            
            r.zincrby(screen_key, input_screen_name, 1)
    
    '''
    try :
        for screen in reversed(screen_list):
            #마지막 값일 경우에는 break
            if 0 == screen_list.index(screen):
                #print "save_screen_flow : break"
                break
            #마지막 값이 아닐 경우에는 app_id를 사용해서
            screen_key = app_id + "_" + screen 
            #print "screek_key : " + screen_key
            next_index = screen_list.index(screen) - 1
            input_screen_name = screen_list[next_index]
            #실제로 screen을 삽입하는 부분
            #screen 존재시
            
            r.zincrby(screen_key, input_screen_name, 1)
    
    except Exception as e:
        print " save_screen_flow exception : " + str(e)
        
    '''
         
  
'''
####################################################################################
system feedback을 저장하는 부분
####################################################################################
'''
            
                    
                
                
    
            
            
         
    
    