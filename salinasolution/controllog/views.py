 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.views.decorators.csrf import csrf_exempt
from django.core.context_processors import request
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


'''
관리자가 페이지에서 해당 앱에 대한 세부정보를 보는 페이지 

def view_admin(request):
   
    debug(TAG, "view_admin_method")
    
    #get feedback aggregation from db
    
    if request.method == 'GET':
        #app_id = 'request.GET[Var.APP_ID]'
        app_id = 'abc'
        
        question_info = FeedbackInfo().make_tmplt_obj(Var.QUESTION, app_id)
        suggestion_info = FeedbackInfo().make_tmplt_obj(Var.SUGGESTION, app_id)
        problem_info = FeedbackInfo().make_tmplt_obj(Var.PROBLEM, app_id)
        praise_info = FeedbackInfo().make_tmplt_obj(Var.PRAISE, app_id)
        #get session aggregation from db
    
        session_info = Session.objects.raw('SELECT id, DAY(start_time) as date, COUNT(start_time) as cnt FROM controllog_session GROUP BY DAY(start_time)')
        user_info = Session.objects.raw('SELECT id, DAY(start_time) as date, COUNT(start_time) as cnt FROM (( SELECT * FROM controllog_session GROUP BY user_id ) as user_table) GROUP BY DAY(start_time)')
        
                                                 
                                                 
        return render_to_response('index.html', {
                                                 'question_info': question_info,
                                                 'suggestion_info': suggestion_info,
                                                 'problem_info': problem_info,
                                                 'praise_info': praise_info,
                                                 },
                                  context_instance=RequestContext(request))
                                  
'''


# Create your views here.

TAG = "controllog.views"

r = redis.StrictRedis(host='localhost', port = 6379, db = 0)
'''
사용자의 조작로그를 저장하는 부분
'''
@csrf_exempt
def controllog(request):
    debug(TAG, "start method")
    return_data = None
    
    if request.method == 'POST' :
        
        debug(TAG, "start method")
        print request.raw_post_data
        dic = json.loads(request.raw_post_data)
        pson.save_control_log(dic)
        
         
    return HttpResponse(return_data)


'''
dash board의 정보를 읽어서 개발자에게 주는 부분 
'''
def view_dashboard(request):
    if request.method == 'GET':
        app_id = request.GET[Var.APP_ID]
        query =  " SELECT id, HOUR(start_time) as date, COUNT(start_time) as cnt FROM controllog_session GROUP BY HOUR(start_time) where app_id = %(app_id)s"
        params = {'app_id' : app_id}
        time_session_info = Session.objects.raw(query, params)
        
        #뭘 넘겨야 할지 결정하기
        return render_to_response('index.html', {
                                                 'time_session_info': time_session_info,
                                                 },
                                  context_instance=RequestContext(request))
        
        
        



'''
basic 기능을 제공하는 부분
1차원적인 view 제공
'''
def view_basic(request):
    
    if request.method == 'GET':
        app_id = request.GET[Var.APP_ID]
        #뭘 넘겨야 할지 결정하기
        
    
    return
         
    
    
def view_advanced(request):    
    
    if request.method == 'GET':
        app_id = request.GET[Var.APP_ID]
        focus_var = request.GET[Var.FOCUS_VAR]
        composite_var = request.GET(Var.COMPOSITE_VAR)
        type_var = request.GET(Var.ADVANCED_TYPE)
        
        #system system feedback 제공
        if(type_var == SYSTEM_SYSTEM_TYPE) :
            return
        
        #system user feedback 제공
        elif(type_var == SYSTEM_USER_TYPE) :
            #태웅이에게 넘겨달라고 할때 feedback은 숫자로 넘겨달라고 하자
            params = [focus_var, focus_var]
            Session.objects.raw(' SELECT count(CASE WHEN solved_check = false) as unsolved_feedback, count(CASE WHEN solved_check = true) as solved_feedback, count(device_name), device_name FROM ' +  
            '(SELECT * from feedback_feedback AS feedback, controllog_deviceinfo AS log where feedback.app_id = log.app_id, feedback.user = log.user, feedback.category = %s)'+
            'GROUP BY device_name'
            , params)
            
        
         
    
'''
insight를 보는 부분
준영이는 프로세스가 종료될때 activity랑 같이 보내준다.
액티비티는 a - b - c - d - e 이런 형식으로 온다.
저장은 linked list를 통해서 저장한다.
redis 자체가 persistence하다. 그렇기 때문에  따로 db에 저장하지 않는다.
'''
def view_insight(request):
    if request.method == 'GET':
        
    
    
    
        return 
    
    
    
    