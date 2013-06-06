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
#import django.contrib.auth.views.login


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
    
    '''
    #피드백 내용을 가져오는 부눈
    if request.method == 'GET':
        category = request.GET[Var.CATEGORY]
        feedback_detail = Feedback.objects.filter(category = category)
                                                 
    '''  
                                               
    return render_to_response('feedback.html', {
                                                'feedback_detail':feedback_detail
                                                 },
                                  context_instance=RequestContext(request))
                                  
                
    
    
def view_register(request):
    #get feedback aggregation from db
                                                 
                                                 
    return render_to_response('register.html', {
                                                 },
                                  context_instance=RequestContext(request))
    
    
def view_contact(request):
    #get feedback aggregation from db
                                                 
                                                 
    return render_to_response('download.html', {
                                                 },
                                  context_instance=RequestContext(request))
    
def view_about(request):
    
   
    
    
    #get feedback aggregation from db                                                 
                                                 
    return render_to_response('about.html', {
                                                 },
                                  context_instance=RequestContext(request))
    
    
    
def view_advanced(request):
    if request.method == 'GET':
        
        app_id = request.GET[Var.APP_ID]
        #focus_var = request.GET[Var.FOCUS_VAR]
        #composite_var = request.GET(Var.COMPOSITE_VAR)
        
        query = 'SELECT seq, x, count(device_name) as y, SUM(if(solved_check = 1,1,0)) as solved, SUM(if(solved_check = 0,1,0)) as unsolved FROM (SELECT seq, device_name, solved_check  from feedback_feedback AS feedback, controllog_deviceinfo AS log where feedback.appuser_id = log.user_id) as join_table GROUP BY device_name;'
        
        
        params = [app_id]
        graph_data = Feedback.objects.raw(query)
        
        #focuas var/ comsite var 추가
        return render_to_response('advanced.html', {
                                                 'graph_data': graph_data,
                                                 },
                                  context_instance=RequestContext(request))


    
'''
    if request.method == 'GET':
        app_id = request.GET[Var.APP_ID]
        #피드백 정보를 디비에서 가져오는 부분
        question_info = FeedbackInfo().make_tmplt_obj(Var.QUESTION, app_id)
        suggestion_info = FeedbackInfo().make_tmplt_obj(Var.SUGGESTION, app_id)
        problem_info = FeedbackInfo().make_tmplt_obj(Var.PROBLEM, app_id)
        praise_info = FeedbackInfo().make_tmplt_obj(Var.PRAISE, app_id)
        #get session aggregation from db
         #세션 정보를 디비에서 가져오는 부분
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
    
'''
def search_page(request):
    form = SearchForm()
    feedbacks = []
    show_results = False
    if request.GET['query'].strip():
        show_results = True
        query = request.GET['query'].strip()
        if query:
            form = SearchForm({'query' : query})
            feedbacks = \
                Feedback.objects.filter(contents__icontains=query)[:10]
    varaibles = RequestContext(request, {'form':form,
                                         'feedbacks':feedbacks,
                                         'show_results':show_results,
                                         'show_tags': True,
                                         'show_user': True,
                                         }
                               )
                
    return render_to_response('search.html', varaibles)
'''    
        
 
