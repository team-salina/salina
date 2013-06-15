 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db.models import Count
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution.debug import debug
from salinasolution.userinfo.models import AppUser, App
from salinasolution.feedback.models import Feedback, FeedbackComment, FeedbackVote, PraiseScore, Reply, ReplyComment, ReplyEvaluation, ReplyVote,\
    FeedbackContext
from salinasolution.controllog.models import  DeviceInfo, Session
import salinasolution.var as Var
from salinasolution.templetobj import FeedbackInfo
from django.core.context_processors import request
from salinasolution import pson 
import simplejson as json
from django.views.decorators.csrf import csrf_protect, csrf_exempt
from salinasolution.userinfo.form import RegistrationForm
from django.contrib.auth.models import User
from django.http import HttpResponse, HttpResponseRedirect
from django.contrib.auth.decorators import login_required
from django.core import serializers
from django.template.context import RequestContext
import redis
import inspect
from django.core.serializers.json import DjangoJSONEncoder

r = redis.StrictRedis(host = 'localhost', port=Var.REDIS_PORT_NUM, db=0)

# Create your views here.

TAG = "adminpage.views"

'''
개발자 등록하는 부분
'''
@csrf_protect
def register_page(request):
    if request.method == 'POST':
        print "POST"
        print request.POST['password1']
        print request.POST['password2']
        form = RegistrationForm(request.POST)
        print form.errors
        if form.is_valid():
            print "form valid"
            user = User.objects.create_user(
               username = form.cleaned_data['username'],
               password = form.cleaned_data['password1'],
               email = form.cleaned_data['email'] 
            )
            print "register_success"
            return HttpResponseRedirect('/')
    return render_to_response('registration/register.html', {
                                                 },
                                  context_instance=RequestContext(request))    
        
def view_admin(request):
    manager_app_info  = []
    return render_to_response('developer.html', {'manager_app_info' : manager_app_info
                                                 },
                                  context_instance=RequestContext(request))
@login_required
def view_developer(request): 
        manage_app =[]
        if request.method == 'GET': 
                params = [str(request.user.pk)]            
                query = "SELECT COUNT(app_id) as feedback_num, app.app_id, app_name, create_date, icon_url, version FROM (SELECT * FROM userinfo_app where user_id = %s) as app LEFT JOIN (SELECT * FROM feedback_feedback where pub_date > CURDATE()) as feedback USING(app_id) GROUP BY feedback.app_id;"
                #query = "SELECT COUNT(feedback.app_id) as today_feedback, app.app_id, app_name, create_date, icon_url, version FROM (SELECT * FROM userinfo_app where user_id = 1) as app LEFT JOIN (SELECT * FROM feedback_feedback where pub_date > CURDATE()) as feedback  USING(app_id) GROUP BY feedback.app_id;"
                manage_app = App.objects.raw(query, params)
                return render_to_response('developer.html', {'manage_app' : manage_app
                                                             },
                                              context_instance=RequestContext(request))
    
@login_required
def view_app_home(request):
    #app에 대한 통계정보 가져오는 부분 
    #현재는 클라이언트에서 받아오지 않으므로 주석처리
    session_data = []
    user_data = []
    app = ""
    if request.method == 'GET' :
            app_id = request.GET[Var.APP_ID]
            params = [app_id]
            session_query = "SELECT id, HOUR(start_time) as hour, COUNT(start_time) as cnt FROM (SELECT * FROM controllog_session where start_time > CURDATE()) as time_table  where app_id = %s GROUP BY HOUR(start_time);"
            user_query = "SELECT id, HOUR(start_time) as hour, COUNT(start_time) as cnt FROM (SELECT * FROM controllog_session where start_time > CURDATE() GROUP BY user_id) as time_table  where app_id = %s GROUP BY HOUR(start_time);"
            
            app = App.objects.get(pk = app_id)
            session_data = Session.objects.raw(session_query, params)
            user_data = Session.objects.raw(user_query, params)
    '''
    try :  
        if request.method == 'GET' :
            app_id = request.GET[Var.APP_ID]
            params = [app_id]
            session_query = "SELECT id, HOUR(start_time) as hour, COUNT(start_time) as cnt FROM (SELECT * FROM controllog_session where start_time > CURDATE()) as time_table  where app_id = %s GROUP BY HOUR(start_time);"
            user_query = "SELECT id, HOUR(start_time) as hour, COUNT(start_time) as cnt FROM (SELECT * FROM controllog_session where start_time > CURDATE() GROUP BY user_id) as time_table  where app_id = %s GROUP BY HOUR(start_time);"            
            app = App.objects.get(pk = app_id)
            session_data = Session.objects.raw(session_query, params)
            user_data = Session.objects.raw(user_query, params)
    except Exception as e:
        print "exception : " + str(e)
    
    '''
    return render_to_response('app-home.html', {  
                                                   'user_data':user_data,
                                                  'session_data':session_data,
                                                   'app':app
                                                  },
                                  context_instance=RequestContext(request))
    

@login_required
def view_real_voice(request):
    if request.method == 'GET' :
        app_id = request.GET[Var.APP_ID]
        params = [app_id]   
        raw_query = "SELECT * FROM (SELECT * FROM feedback_feedback WHERE app_id = 'noon_date') as feedback, feedback_feedbackcontext WHERE feedback.seq = feedback_feedbackcontext.feedback_id ORDER BY function_name ASC;"
        
        feedback = Feedback.objects.raw(raw_query)        
        '''            
        feedback=Feedback.objects.filter(app = app_id)
        for feed in feedback :
            feedbackcontext = feed.feedbackcontext_set.all()
            print feedbackcontext[0].function_name
        
        for feed in feedback :
            feedbackcontext = feed.feedbackcontext_set.all()
            print feedbackcontext.app_version
        '''        
        return render_to_response('real_voice.html', {
                                                'feedback':feedback,
                                                'app_id':app_id,
                                                 },
                                  context_instance=RequestContext(request))

        
def view_feedback(request):
   
    debug(TAG, "view_admin_method")
    feedback_detail = None            
    return render_to_response('feedback.html', {
                                                'feedback_detail':feedback_detail
                                                 },
                                  context_instance=RequestContext(request))
                                  
                
    
    
def view_register(request):              
    return render_to_response('register.html', {
                                                 },
                                  context_instance=RequestContext(request))
    
    
def view_contact(request):        
    return render_to_response('download.html', {
                                                 },
                                  context_instance=RequestContext(request))
    
def view_about(request):              
    return render_to_response('about.html', {
                                                 },
                                  context_instance=RequestContext(request))
    
    
def view_advanced(request):
    if request.method == 'GET':
        
        try:
            request_type = request.GET['request_type']
        except Exception, e:
            request_type = 'render_request'
        
        if request_type == 'ajax_request':
            try:
                
                app_id = request.GET[Var.APP_ID]
                focus = request.GET[Var.FOCUS]
                composite = request.GET[Var.COMPOSITE]
                query_type = request.GET['query_type']
                if query_type == 'user' :
                    query = 'SELECT seq, ' +  focus +' as x, count('+ focus +') as y, SUM(if(solved_check = 1,1,0)) as solved, SUM(if(solved_check = 0,1,0)) as unsolved FROM (SELECT *  from feedback_feedback AS feedback, feedback_feedbackcontext AS context where feedback.seq = context.feedback_id and feedback.category = "'+ composite + '" and feedback.app_id = "'+app_id+'") as join_table GROUP BY '+focus+' ORDER BY y desc;'
                    print query
                    
                    graph_data = Feedback.objects.raw(query)
                    graph_data =  Var.todict(graph_data)
                    return HttpResponse(json.dumps(graph_data), mimetype="application/json")
                elif query_type == 'system':
                    data_dic = []
                    
                    focus_attrs = DeviceInfo.objects.raw('SELECT id, ' + focus + ' as focus_column FROM controllog_deviceinfo WHERE app_id = "' +app_id +  '" GROUP BY ' + focus + ' ORDER BY ' + focus + ' desc;' );
                    composite_attrs = DeviceInfo.objects.raw('SELECT id, ' + composite + ' as composite_column FROM controllog_deviceinfo WHERE app_id = "' +app_id + '" GROUP BY ' + composite + ' ORDER BY ' + focus + ' desc;');
                    for focus_attr in focus_attrs:
                            #query = 'SELECT id, ' + focus + ' AS x, '
                            query = 'SELECT id, ' + focus + ' AS x, '
                            for composite_attr in composite_attrs:
                                query += 'SUM(if(' + composite + ' = "' + composite_attr.composite_column + '" ,1,0)) AS "' + composite_attr.composite_column + '",'
                            query = query[0:len(query)-1]    
                            query += ' FROM controllog_deviceinfo where ' + focus + ' = "' + focus_attr.focus_column + '";'
                            print query
                            graph_data = DeviceInfo.objects.raw(query)
                            graph_data =  Var.todict(graph_data)
                            data_dic.append(graph_data)
                    return HttpResponse(json.dumps(graph_data), mimetype="application/json")
            except Exception, e:
                print str(e)
        else :
            app_id = request.GET[Var.APP_ID]
            return render_to_response('advanced.html', {
                                                        'app_id': app_id,
                                                        }, context_instance=RequestContext(request))
            

def view_insight(request):
    if request.method == 'GET' :
        return render_to_response('insight.html', { },
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
        
        
def handle_graph_ajax_request(request):
    
    if request.method == 'GET':        
        query = 'SELECT seq, device_name as x, count(device_name) as device_num, SUM(if(solved_check = 1,1,0)) as solved, SUM(if(solved_check = 0,1,0)) as unsolved FROM (SELECT seq, device_name, solved_check  from feedback_feedback AS feedback, controllog_deviceinfo AS log where feedback.appuser_id = log.user_id) as join_table GROUP BY device_name;'
        graph_data = Feedback.objects.raw(query)
        graph_data =  Var.todict(graph_data)
        return HttpResponse(json.dumps(graph_data), mimetype="application/json")  
        
        

        
        


    

        
 

