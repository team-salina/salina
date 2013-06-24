 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db.models import Count, Avg
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution.debug import debug
from salinasolution.userinfo.models import AppUser, App
from salinasolution.feedback.models import Feedback, FeedbackComment, FeedbackVote, PraiseScore, Reply, ReplyComment, ReplyEvaluation, ReplyVote,   FeedbackContext
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
import salinasolution.ast as ast

r = redis.StrictRedis(host = 'localhost', port=Var.REDIS_PORT_NUM, db=0)

# Create your views here.

TAG = "adminpage.views"
@csrf_exempt
def register_reg_id(request):
    print "asfasdfsdafdasfasdf"
    try:
        if request.method == 'POST':
            
            
            reg_id = request.POST['reg_id']
            device_key = request.POST['device_key']
            print len(reg_id) 
            print  device_key
            
            appuser=AppUser.objects.get(device_key = device_key)
            appuser.reg_id = reg_id 
            appuser.save()
            
            return HttpResponse("success")
        else :
            return HttpResponse("fail")
    except Exception, e:
        print str(e)
        


def view_home(request):
    return render_to_response('registration/home.html', RequestContext(request, {
    }))
    


'''
개발자 등록하는 부분
'''
@csrf_protect
def register_page(request):
    if request.method == 'POST':
        print request.POST['password1']
        print request.POST['password2']
        form = RegistrationForm(request.POST)
        print form.errors
        if form.is_valid():
           print "register success"
           user = User.objects.create_user(
               username = form.cleaned_data['username'],
               password = form.cleaned_data['password1'],
               email = form.cleaned_data['email'] 
            )
           return HttpResponseRedirect('/')
       
    else :
        form = RegistrationForm()
        
    variables = RequestContext(request, {
        'form':form
    })    
            
    return render_to_response('registration/register.html', variables)  
  
        
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
    return render_to_response('real_voice.html', {  
                                                   'user_data':user_data,
                                                  'session_data':session_data,
                                                   'app':app,
                                                   'app_id':app_id,
                                                  },
                                  context_instance=RequestContext(request))
    
'''       
        has_category = request.GET.has_key(Var.CATEGORY)
        has_screen = request.GET.has_key('screen')
        has_function = request.GET.has_key('function')

        if has_category :
            category = request.GET[Var.CATEGORY]
            if has_screen:
                return
            #    
            else :
                screen_infos = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = category).values('screen_name').annotate(scount=Count('screen_name')).order_by('scount')
                
                feedbacklist=Feedback.objects.filter(app__pk = app_id, category = category)
                
                return render_to_response('app-home.html', {  
                                                   'screen_infos'
                                                  },
                                  context_instance=RequestContext(request))
                
                
                   
        #맨처음    
        else :
            question_num= Feedback.objects.filter(app__pk = app_id, category = Var.QUESTION).count()
            suggestion_num= Feedback.objects.filter(app__pk = app_id, category = Var.SUGGESTION).count()
            problem_num= Feedback.objects.filter(app__pk = app_id, category = Var.PROBLEM).count()
            praise_num= Feedback.objects.filter(app__pk = app_id, category = Var.PRAISE).count()
            
            return render_to_response('app-home.html', {  
                                                   'question_num':question_num,
                                                  'suggestion_num':suggestion_num,
                                                   'problem_num':problem_num,
                                                   'praise_num':praise_num,
                                                  },
                                  context_instance=RequestContext(request))
'''      
    

@login_required
def view_real_voice(request):
    '''
    try :
        if request.method == 'GET' :
            #무조건 두가지 값이 존재한다.
            request.GET['screen_name'] = '오늘의 카드'
            request.GET['function_name'] = '프로필 카드 선택'
            app_id = request.GET[Var.APP_ID]
            has_request_type = request.GET.has_key('request_type')
            if has_request_type:
                request_type = request.GET['request_type']
                if request_type == 'screen':
                    category = request.GET[Var.CATEGORY]
                    if category == 'evaluation':
                        screens = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = category).annotate(evaluation_avg=Avg('feedback__praise_score')).order_by('score_avg').reverse().values('score_avg','screen_name')
                    else :
                        screens = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = category).values('screen_name').annotate(scount=Count('screen_name')).order_by('scount').reverse()
                    
                    return render_to_response('admin_more_info/more_screens.html', {  
                                                               'screens':screens,
                                                               'app_id':app_id,
                                                              },
                                              context_instance=RequestContext(request))
                elif request_type == 'function':
                    category = request.GET[Var.CATEGORY]
                    screen_name = request.GET['screen_name']
                    functions = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = category, screen_name = screen_name).values('function_name').annotate(scount=Count('function_name')).order_by('scount').reverse()
                    return render_to_response('admin_more_info/more_functions.html', {  
                                                           'functions':functions,
                                                           'app_id':app_id,
                                                          },
                                          context_instance=RequestContext(request)) 
                elif request_type == 'feedback':                    
                    category = request.GET[Var.CATEGORY]
                    has_screen_name = request.GET.has_key('screen_name')
                    has_function_name = request.GET.has_key('function_name')
                    feedbacks = FeedbackContext()
                    
                    if has_screen_name:
                        screen_name = request.GET['screen_name']
                        print screen_name
                        if has_function_name :
                            print "asdfadsfadsfasdfadsfdasfffsdafasdfasdfasdfasdfasdfasdf"
                            function_name = request.GET['function_name']
                            print "asdfadsfadsfasdfadsfdasfffsdafasdfasdfasdfasdfasdfasdf"
                            feedbacks = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = category, function_name = function_name, screen_name = screen_name).select_related()
                        else :
                            feedbacks = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = category, screen_name = screen_name).select_related()
                            
                    else :
                        feedbacks = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = category).select_related()
                        
                    for feedback_info in feedbacks:
                        vote_num = FeedbackVote.objects.filter(feedback = feedback_info.feedback).count()
                        print "asfasfdsafasdfdsafasdfasddasfdasfasdfadsf"
                        reply_num = Reply.objects.filter(feedback = feedback_info.feedback).count()
                        setattr(feedback_info.feedback, "vote_num", vote_num)
                        setattr(feedback_info.feedback, "reply_num", reply_num)
                    
                    for feedback_info in feedbacks:
                        print feedback_info.feedback.vote_num
                        print feedback_info.feedback.reply_num
                          
                              
                    return render_to_response('admin_more_info/more_feedbacks.html', {  
                                                           'feedbacks':feedbacks,
                                                           'app_id':app_id,
                                                          },
                                      context_instance=RequestContext(request))
            #아무것도 없을때                
            else :
                return render_to_response('real_voice.html', {  
                                                       'app_id':app_id,
                                                      },
                                      context_instance=RequestContext(request))
    except Exception, e:
    '''
    try :
        print "Asfasdfdsafasddsafdsafdasfdsafdasfsdadsadsafsdaf"
        category = request.GET[Var.CATEGORY]
        app_id = request.GET[Var.APP_ID]
        has_request_type = request.GET.has_key('request_type')
        if has_request_type :
            request_type = request.GET['request_type']
            print "req type " + request_type
            if request_type == 'screen':
                
                print "asdfasdfdasasddsafasdfsdafasdfasdf"
                screens = FeedbackContext.objects.filter(feedback__app__pk = app_id).values('screen_name').annotate(scount=Count('screen_name')).order_by('scount').reverse()
                return render_to_response('admin_more_info/more_screens.html', {  
                                                                       'screens':screens,
                                                                       'app_id':app_id,
                                                                      },
                                                      context_instance=RequestContext(request))
            elif request_type == 'function':
                print "ASfasfdsasadfasfsadfasfasdfasdfsdaasdf"
                functions = FeedbackContext.objects.filter(feedback__app__pk = app_id).values('function_name').annotate(scount=Count('function_name')).order_by('scount').reverse()
                
                return render_to_response('admin_more_info/more_functions.html', {  
                                                                   'functions':functions,
                                                                   'app_id':app_id,
                                                                  },
                                                  context_instance=RequestContext(request))
                 
            elif request_type == 'feedback':
                feedbacks = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = 'suggestion').select_related()
                print feedbacks.count()
                return render_to_response('admin_more_info/more_feedbacks.html', {  
                                                                   'feedbacks':feedbacks,
                                                                   'app_id':app_id,
                                                                  },
                                              context_instance=RequestContext(request))
            else :
                screens = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__category = 'suggestion').values('screen_name').annotate(scount=Count('screen_name')).order_by('scount').reverse()
                return render_to_response('admin_more_info/more_screens.html', {  
                                                                       'screens':screens,
                                                                       'app_id':app_id,
                                                                      },
                                                      context_instance=RequestContext(request))
                
        else :
            print "afsadfsdaf"
            return render_to_response('admin_more_info/more_screens.html', {  
                                                                       'screens':screens,
                                                                       'app_id':app_id,
                                                                      },
                                                      context_instance=RequestContext(request))
            
    except Exception, e:
            print str(e)
 

        
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
                graph_data = ''
                if query_type == 'user' :
                    query = 'SELECT seq, ' +  focus +' as x, count('+ focus +') as y, SUM(if(solved_check = 1,1,0)) as solved, SUM(if(solved_check = 0,1,0)) as unsolved FROM (SELECT *  from feedback_feedback AS feedback, feedback_feedbackcontext AS context where feedback.seq = context.feedback_id and feedback.category = "'+ composite + '" and feedback.app_id = "'+app_id+'") as join_table GROUP BY '+focus+' ORDER BY y desc;'
                    print query
                    
                    graph_data = Feedback.objects.raw(query)
                    graph_data =  Var.todict(graph_data)
                    return HttpResponse(json.dumps(graph_data), mimetype="application/json")
                elif query_type == 'system':
                    data_dic = []
                    print 'system'
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
    try:
         app_id = ''
         destination_activity = ''
         graph_num = ''
         request_type = ''
         if request.method == 'GET' :
            try :
                app_id = request.GET[Var.APP_ID]
                destination_activity = request.GET[Var.DESTINATION_ACTIVITY]
                #보여줘야 할 그래프의 갯수
                graph_num = request.GET[Var.DESTINATION_ACTIVITY]
                #그려줄 노드의 개
                node_num = 0
                
                print request_type + "sdfasfasdf"
                node_num = request.GET['node_num']
            except Exception, e:
                #node num이 없는 경우 default 4로 지정
                node_num = 4
                app_id= 'noon_date'
                #request_type = ''
                
            if request.GET.has_key('request_type'):
                request_type = request.GET['request_type']
            else :
                request_type = ''
            
            screen_key = app_id + "_" + destination_activity       
            
            graph_attr = {}
            graph_list = []
            graph = []
            dic = {"activity_name":"","visit_num":0}
            #data_collect_flag = False
            # 애초에 데이터가 없으면 return
            ''' 
            if r.exists(screen_key) == False:
                return
            '''
            #해당 스크린에 대한 data가 존재한다면
            print "req    " + request_type
            if request_type == 'activity_flow': 
                for i in graph_num :
                    screen_key = app_id + "_" + destination_activity
                    dic['activity_name'] = destination_activity
                    dic['visit_num'] = 0
                    graph.append(dic)
                    
                    for j in node_num :
                        #해당 화면이 더이상 없을때는 해당 그래프에 노드를 추가시키는 작업을 중지
                        if r.exists(screen_key) == False:
                            #data_collect_flag = True
                            break
                        activity_list = r.zrange(screen_key, 0, -1, withscores = True)
                        reverse_activity_list = activity_list.reverse() 
                        #큰것부터 차례로 뽑아야함
                        #처음 그래프를 가져올때는 무조건 
                        #list에 대이터가 없는 경우 예외처리
                        if j == 0:
                            #screen_key는 존재하나 data가 없는 경우
                            if (len(reverse_activity_list) - 1) >= i:
                                dic['activity_name'] = reverse_activity_list[i][0]
                                dic['visit_num'] = reverse_activity_list[i][1]
                            else :
                                break
                        #두번째 이상부터는 무조건 처음 데이터를 가져와야됨 그게 가장 많이 지나간 경로이므로
                        else :
                            dic['activity_name'] = reverse_activity_list[0][0]
                            dic['visit_num'] = reverse_activity_list[0][1]    
                        
                        #현재 액티비티가 리스트에 존재하지 않는다면
                        if dic['activity_name'] in graph_attr == False:
                            graph_attr[dic['activity_name']] = len(graph_attr)
                        
                        graph.append(dic)
                        screen_key = app_id + "_" + dic['activity_name']
                    #graph에 데이터가 없다면 추가않함
                    if len(graph) != 0:
                       graph_list.append(graph)
                       
                #client를 위한  json 데이터 생성
                nodeDataArray = []
                keys = graph_attr.keys()
                #activity name -> key
                for key in keys:
                    nodeData = {"key" : graph_attr[key], "category":"Source", "text" : key}
                    nodeDataArray.append(nodeData)
                            
                return HttpResponse(json.dump(graph_list), mimetype="application/json") 
            
            elif request_type == 'flow_activity' :
                if request.GET.has_key('destination_activity') :
                    destination_activity=request.GET['destination_activity']
                    result = Var.get_default_result(destination_activity)
                    print result
                    return HttpResponse(result)
                    
            
            return render_to_response('insight.html', { },
                                      context_instance=RequestContext(request))
    except Exception, e:
            print str(e)
        
        
def view_destination_activity_flow(request):
    print "feedback"
    if request.method == 'GET' :
        app_id = request.GET[Var.APP_ID]
        destination_activity = request.GET[Var.DESTINATION_ACTIVITY]
        #보여줘야 할 그래프의 갯수
        graph_num = request.GET[Var.DESTINATION_ACTIVITY]
        #그려줄 노드의 개
        request_type = ''
        node_num = 0
        
        try :
            request_type = request.GET['request_type']
            node_num = request.GET['node_num']
        except Exception, e:
            #node num이 없는 경우 default 4로 지정
            node_num = 4
            request_type = ''
            
        
        screen_key = app_id + "_" + destination_activity       
        
        graph_attr = {}
        graph_list = []
        graph = []
        dic = {"activity_name":"","visit_num":0}
        #data_collect_flag = False
        # 애초에 데이터가 없으면 return
        ''' 
        if r.exists(screen_key) == False:
            return
        '''
        #해당 스크린에 대한 data가 존재한다면
        if request_type == 'activity_flow': 
            for i in graph_num :
                screen_key = app_id + "_" + destination_activity
                dic['activity_name'] = destination_activity
                dic['visit_num'] = 0
                graph.append(dic)
                
                for j in node_num :
                    #해당 화면이 더이상 없을때는 해당 그래프에 노드를 추가시키는 작업을 중지
                    if r.exists(screen_key) == False:
                        #data_collect_flag = True
                        break
                    activity_list = r.zrange(screen_key, 0, -1, withscores = True)
                    reverse_activity_list = activity_list.reverse() 
                    #큰것부터 차례로 뽑아야함
                    #처음 그래프를 가져올때는 무조건 
                    #list에 대이터가 없는 경우 예외처리
                    if j == 0:
                        #screen_key는 존재하나 data가 없는 경우
                        if (len(reverse_activity_list) - 1) >= i:
                            dic['activity_name'] = reverse_activity_list[i][0]
                            dic['visit_num'] = reverse_activity_list[i][1]
                        else :
                            break
                    #두번째 이상부터는 무조건 처음 데이터를 가져와야됨 그게 가장 많이 지나간 경로이므로
                    else :
                        dic['activity_name'] = reverse_activity_list[0][0]
                        dic['visit_num'] = reverse_activity_list[0][1]    
                    
                    #현재 액티비티가 리스트에 존재하지 않는다면
                    if dic['activity_name'] in graph_attr == False:
                        graph_attr[dic['activity_name']] = len(graph_attr)
                    
                    graph.append(dic)
                    screen_key = app_id + "_" + dic['activity_name']
                #graph에 데이터가 없다면 추가않함
                if len(graph) != 0:
                   graph_list.append(graph)
                   
            #client를 위한  json 데이터 생성
            nodeDataArray = []
            keys = graph_attr.keys()
            #activity name -> key
            for key in keys:
                nodeData = {"key" : graph_attr[key], "category":"Source", "text" : key}
                nodeDataArray.append(nodeData)
                        
            return HttpResponse(json.dump(graph_list), mimetype="application/json") 
        
        else :
            result = Var.get_default_result()
            return HttpResponse(json.dump(result), mimetype="application/json")
            
            

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
        
        
  
    

        

        
        


    

        
 

