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
from salinasolution.datawrite import save_screen_flow
'''
controllog create 하는 연산
'''


#Redis Port num은 6379

r = redis.StrictRedis(host = 'localhost', port=Var.REDIS_PORT_NUM, db=0)

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
        screen_obj = {'screen_name': '', 'visit_num': 0}
        
        screen_obj['screen_name'] = input_screen_name
        #실제로 screen을 삽입하는 부분
        #screen 존재시
        if r.exists(screen_key) :
            for i in r.llen(screen_key):
                cur_screen_str = r.lindex(screen_key, i)
                cur_screen_obj = ast.literal_eval(cur_screen_str)
                if cur_screen_obj['screen_name'] == input_screen_name :
                    cur_screen_obj['visit_num'] = cur_screen_obj['visit_num'] + 1
                    r.lset(screen_key, i, cur_screen_obj)
                else :
                    r.rpush(screen_key, screen_obj)
        else :
            r.rpush(screen_key, screen_obj)

#controllog를 저장하는 부분
@csrf_exempt
def save_system_feedback(request):
    if request.method == 'POST':
        
        dic = json.loads(request.raw_post_data)
        dic_key_list = dic.keys()
        app_id = ""
        
        for key in dic_key_list:
            '''
                dic에 만들어서 redis에 저장하자 
                                    그리고 객체로 저장 
            '''
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
                return
            
            
                    
                    
                
    
            
            
         
    
    