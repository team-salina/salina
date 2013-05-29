 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.views.decorators.csrf import csrf_exempt
from salinasolution.debug import debug
from salinasolution.feedback.models import Feedback
from salinasolution.controllog.models import Session
from salinasolution.userinfo.models import App, Manager, User
from django.core import serializers
from django.http import HttpResponse
import os
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution import pson, templetobj
import redis
import json
import salinasolution.var as Var
import salinasolution.templetobj
from salinasolution.var import SYSTEM_SYSTEM_TYPE, SYSTEM_USER_TYPE
import redis
import ast
from django.conf.locale import tr

'''
controllog create 하는 연산
'''

#Redis Port num은 6379

r = redis.StrictRedis(host = 'localhost', port=Var.REDIS_PORT_NUM, db=0)


def save_trigger_event(event_list, app_id):
    
    reverse_event_list = event_list.reverse()
    
    ex_screen_name = ''
    event_list = []
    
    for event_dic in reverse_event_list:
        
        cur_screen_name = event_dic['screen_name']
        event_name = event_dic['event_name']
        
        #처음 인경우
        if reverse_event_list(event_dic) == 0 :
            ex_screen_name = cur_screen_name
       
       #같다면
        if ex_screen_name == cur_screen_name :
            event_list.append(event_name)
        
        #다르다면    
        else :
            screen_key = app_id + "_" + cur_screen_name
            #redis에 추가하는 부분
            for event in event_list:
                r.zincrby(screen_key, event, 1)
            event_list = []
            ex_screen_name = cur_screen_name
        
        
        #if i == len(reverse_event_list) :
        


def save_screen_flow(screen_list, app_id):
    
    reverse_screen_list  = screen_list.reverse()
    
    for screen in reverse_screen_list:
        #마지막 값일 경우에는 break
        if len(list)-1 == reverse_screen_list.index(screen):
            break
        
        #마지막 값이 아닐 경우에는 app_id를 사용해서
        screen_key = screen + app_id
        next_index = reverse_screen_list.index(screen) + 1
        input_screen_name = reverse_screen_list[next_index]
        #실제로 screen을 삽입하는 부분
        #screen 존재시
        '''
        input_flag = False
        if r.exists(screen_key) :
            for cur_screen_obj in r.zrange(screen_key, 0, -1, withscores=True) :
                if cur_screen_obj[0] == input_screen_name :
                    r.zincrby(screen_key, input_screen_name, 1)
                    input_flag = True
                    break
        #screen이 삽입 되지 않았다면 zadd 시킬것
        if input_flag == False:
            r.zadd(screen_key, input_screen_name, 1)
        '''
        r.zincrby(screen_key, input_screen_name, 1)
         

#controllog를 저장하는 부분
@csrf_exempt
def save_system_feedback(request):
    
    print "start save_system_feedback"
    
    if request.method == 'POST':
        
        dic = json.loads(request.raw_post_data)
        dic_key_list = dic.keys()
        app_id = ""
        
        for key in dic_key_list:
            
            #Session인 경우 redis에 저장
            if key == 'SessionList':
                session_list = dic[key]
                for session in session_list:
                    r.rpush(key, session)
                    app_id = session[Var.APP_ID]
            #DeviceInfo인 경우 redis에 저장    
            elif key == 'DeviceInfo':
                r.rpush(key, dic[key])
            
            elif key == 'ScreenFlow':
                save_screen_flow(dic[key], app_id)
            #여긴 준영이랑 논의
            elif key == 'Event':
                save_trigger_event(dic[key], app_id)
                
                
            
            
            
                    
                    
                
    
            
            
         
    
    