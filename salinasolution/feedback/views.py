 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.views.decorators.csrf import csrf_exempt, csrf_protect
from salinasolution.debug import debug
import json
import  salinasolution.var as Var
from salinasolution.controllog.models import Session, DeviceInfo
from salinasolution.feedback.models import Feedback, FeedbackContext
from salinasolution.userinfo.models import App,  AppUser
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution import pson
import salinasolution.redisread as redisread
import redis 
import ast

TAG = "feedback.views"

#every python file has this TAG, this TAG means the path of salina framework


# request로부터 feedback 정보를 얻고, db로 저장하는 메서드
# create_feedback
r = redis.StrictRedis(host = 'localhost', port=Var.REDIS_PORT_NUM, db=0)


'''
####################################################################################
user feedback을 저장하는 부분
####################################################################################
'''

@csrf_exempt
def save_user_feedback(request):
    
    print "save_user_feedback"
    if request.method == 'POST':
        print "post"
        
        feedback = Feedback()
        
        dic = str(json.loads(request.raw_post_data))
        dic = ast.literal_eval(dic)
        dic = dic["user_feedback"]
        
        
        dic_key_list = dic.keys()
        print dic_key_list
        try :    
            for key in dic_key_list:
                if key == 'Feedback':
                    instance = pson.make_instance_by_name(key)
                    instance = pson.dic_to_obj(dic[key], instance)
                    instance = instance.auto_save()
                    feedback = instance
                    
                elif key == 'FeedbackContext':
                    instance = FeedbackContext()
                    instance = pson.dic_to_obj(dic[key], instance)
                    instance.feedback = feedback
                    instance.save()    
        except Exception as e:
            print e
        
        return "success"
         
    return "fail" 


'''
####################################################################################
user feedback을 저장하는 부분
####################################################################################
'''

'''
####################################################################################
각 웹페이지 마다 데이터 전송하는 부분
####################################################################################
''' 

'''                
def view_dashboard(request):
    if request.method == 'GET':
        app_id = request.GET[Var.APP_ID]
        #query =  "SELECT id, HOUR(start_time) as hour, COUNT(start_time) as cnt FROM controllog_session GROUP BY HOUR(start_time) where app_id = %(app_id)s"
        query = "SELECT id, HOUR(start_time) as hour, COUNT(start_time) as cnt FROM " 
        +"(SELECT * FROM controllog_session where start_time = CURDATE()) as time_table"
        +" where app_id = 'com.nnoco.dday' GROUP BY HOUR(start_time);"        
        params = [app_id]
        time_session_info = Session.objects.raw(query, params)        
        #뭘 넘겨야 할지 결정하기
        return render_to_response('index.html', {
                                                 'time_session_info': time_session_info,
                                                 },
                                  context_instance=RequestContext(request))
        
        
def view_basic(request):
    if request.method == 'GET':
        app_id = request.GET[Var.APP_ID]
        focus_var = request.GET[Var.FOCUS_VAR]
        composite_var = request.GET(Var.COMPOSITE_VAR)
        #뭘 넘겨야 할지 결정하기
    return
'''

def view_advanced(request):
    if request.method == 'GET':
        
        app_id = request.GET[Var.APP_ID]
        focus_var = request.GET[Var.FOCUS_VAR]
        composite_var = request.GET(Var.COMPOSITE_VAR)
        
        query = 'SELECT device_name, count(device_name), SUM(if(solved_check = 1,1,0)) as solved, SUM(if(solved_check = 0,1,0)) as unsolved FROM'
        + '(SELECT device_name, solved_check  from feedback_feedback AS feedback, controllog_deviceinfo AS log where feedback.user_id = log.user_id) as join_table'
        + 'GROUP BY device_name'
        
        params = [app_id]
        advanced_view = Feedback.objects.raw(query, params)
        
        return render_to_response('index.html', {
                                                 'advanced_view': advanced_view,
                                                 },
                                  context_instance=RequestContext(request))
        
        
        
def view_destination_activity_flow(request):
    print "feedback"
    if request.method == 'GET' :
        app_id = request.GET[Var.APP_ID]
        destination_activity = request.GET[Var.DESTINATION_ACTIVITY]
        graph_num = request.GET[Var.DESTINATION_ACTIVITY]
        node_num = 4
        
        screen_key = app_id + "_" + destination_activity
        
        data_list = []
        dic_list = []
        dic = {"activity_name":"","visit_num":0}
        
        data_collect_flag = False
        
        for i in graph_num :
            for j in node_num :
                if r.exists(screen_key) == False:
                    data_collect_flag = True
                    break
                activity_list = r.zrange(screen_key, 0, -1, withscores = True)
                reverse_activity_list = activity_list.reverse() 
                #큰것부터 차례로 뽑아야함
                dic['activity_name'] = reverse_activity_list[i][0]
                dic['visit_num'] = reverse_activity_list[i][1]
                    
                dic_list.append(dic)
                    
                screen_key = app_id + "_" + dic['activity_name']
            
            data_list.append(dic_list)
            
            if data_collect_flag == True:
                break
            

def view_trigger_function(request):
    
    if request.method == 'GET':
        app_id = request.GET[Var.APP_ID]
        destination_activity = request.GET[Var.DESTINATION_ACTIVITY]
        screen_key = app_id + "_" + destination_activity        
        
        list = r.zrange(screen_key, 0, -1, withscores = True)
        
        #뭘 넘겨야 할지 결정하기
        return render_to_response('index.html', {
                                                 'list': list,
                                                 },
                                  context_instance=RequestContext(request))
    
             
'''
####################################################################################
각 웹페이지 마다 데이터 전송하는 부분
####################################################################################
'''    




         
        
        
        


