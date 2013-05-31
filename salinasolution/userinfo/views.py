 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db.models import Count
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution.debug import debug
from salinasolution.userinfo.models import AppUser
from salinasolution.feedback.models import Feedback, FeedbackComment, FeedbackVote, PraiseScore, Reply, ReplyComment, ReplyEvaluation, ReplyVote
from salinasolution.controllog.models import  DeviceInfo, Session
import salinasolution.var as Var
from salinasolution.templetobj import FeedbackInfo
from django.core.context_processors import request
from salinasolution import pson 
import json
from django.views.decorators.csrf import csrf_protect, csrf_exempt
from salinasolution.userinfo.form import RegistrationForm
from django.contrib.auth.models import User
from django.http import HttpResponse, HttpResponseRedirect
#import django.contrib.auth.views.login


# Create your views here.

TAG = "adminpage.views"   

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
    
    print "sex"
    manager_app_info  = []
    
    return render_to_response('developer.html', {'manager_app_info' : manager_app_info
                                                 },
                                  context_instance=RequestContext(request))
        
def view_developer(request):
   
    debug(TAG, "view_admin_method")
    
    #user id 기반 앱 정보 가져오는 부분    
    manager_app_info = None
    '''
    if request.method == 'GET' :                                             
         manager_id = request.GET[Var.MANAGER_ID]
         manager_app_info = Manager.objects.select_related().filter(manager_id = manager_id)
    '''   
                 
    return render_to_response('developer.html', {'manager_app_info' : manager_app_info
                                                 },
                                  context_instance=RequestContext(request))
    
    
def view_app_home(request):
    
    #app에 대한 통계정보 가져오는 부분 
    #현재는 클라이언트에서 받아오지 않으므로 주석처리  
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
    return render_to_response('app-home.html', {    },
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


    
      
        
 
