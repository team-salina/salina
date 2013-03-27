 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db.models import Count
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution.debug import debug
from salinasolution.userinfo.models import Manager
from salinasolution.feedback.models import Feedback, FeedbackComment, FeedbackVote, PraiseScore, Reply, ReplyComment, ReplyEvaluation, ReplyVote
from salinasolution.controllog.models import Crash, DeviceInfo, Session
from salinasolution.var import Var
from salinasolution.templetobj import FeedbackInfo
from django.core.context_processors import request
from salinasolution import pson 
import json
# Create your views here.

TAG = "adminpage.views"
'''
def view_app(request):
    debug(TAG, "view_app_method")
    if request.method == 'GET':
        user_id = request.GET[Var.USER_ID]
        
        manager_info = Manager.objects.get(user_id = user_id)
        app_info = manager_info.managerappinfo_set.get()
        app_list = []
        
        for feedback in app_info.feedback_set.all:
            app_info = feedback.group_by('category').annotate(ccount=Count('category'))
            app_list.append(app_info)
            
    return render_to_response('', {'app_list':app_list},
                              context_instance=RequestContext(request))
'''    
    
'''
관리자가 페이지에서 해당 앱에 대한 세부정보를 보는 페이지 
''' 
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
    
        session_info = Session.objects.raw('SELECT DAY(start_time) as date, COUNT(start_time) as cnt FROM controllog_session GROUP BY DAY(start_time)')
        #user_info = Session.objects.raw('SELECT start_time as date, COUNT(start_time) as cnt FROM ( SELECT * FROM controllog_session GROUP BY user ) GROUP BY DAY(start_time)')
        debug(TAG, "before_response")
        print session_info
        
        return render_to_response('index.html', {'question_info': question_info,
                                                 'suggestion_info': suggestion_info,
                                                 'problem_info': problem_info,
                                                 'praise_info': praise_info,
                                                 },
                                  context_instance=RequestContext(request))

    
      
        
 
