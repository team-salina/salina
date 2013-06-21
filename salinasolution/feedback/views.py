 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.views.decorators.csrf import csrf_exempt, csrf_protect
from salinasolution.debug import debug
import simplejson as json
import  salinasolution.var as Var
from salinasolution.controllog.models import  DeviceInfo
from salinasolution.feedback.models import Feedback, FeedbackContext, Reply, ReplyComment, FeedbackComment
from salinasolution.userinfo.models import App,  AppUser
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution import pson, mygcm
import salinasolution.redisread as redisread
import redis 
import salinasolution.ast as ast
from django.http import  HttpResponseRedirect
import mimetypes
from django.core.serializers.json import DjangoJSONEncoder
from django.contrib.auth.models import User 
from django.contrib.auth import REDIRECT_FIELD_NAME, login as auth_login
from django.contrib.auth import login, logout
from django.contrib.sessions.models import Session




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
    try:
        if request.method == 'POST':
            print "post"
            
            feedback = Feedback()
            
            print json.loads(request.raw_post_data)        
            dic = str(json.loads(request.raw_post_data))
            dic = ast.literal_eval(dic)
            dic = dic["user_feedback"]
            
            key = 'Feedback'
            instance = pson.make_instance_by_name(key)
            instance = pson.dic_to_obj(dic[key], instance)
            instance = instance.auto_save()
            feedback = instance
            print  feedback   
            key = 'FeedbackContext'    
            instance = FeedbackContext()
            instance = pson.dic_to_obj(dic[key], instance)
            instance.feedback = feedback
            instance.save()
            '''
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
            
                return HttpResponse("success")
            ''' 
            return HttpResponse("success")
    except Exception, e:
        print str(e) 

'''
####################################################################################
user feedback을 저장하는 부분
####################################################################################
'''



    
             
'''
####################################################################################
community부분
####################################################################################
'''    

        



def view_my_feedback(request):
    if request.method == 'GET':
        
        app_id = request.GET[Var.APP_ID]
        device_key = request.GET[Var.DEVICE_KEY]
        #feedbackcontexts = FeedbackContext.objects.filter(feedback__appuser__device_key = device_key).filter(feedback__app__app_id = app_id)
        myfeedbacks = FeedbackContext.objects.filter(feedback__app__pk = app_id, feedback__appuser__pk = device_key).select_related()
        
        return render_to_response('sdk/my_feedback.html', {
                                                 'myfeedbacks': myfeedbacks,
                                                 },
                                  context_instance=RequestContext(request))

@csrf_exempt
def view_feedback_detail(request):
    view_type = request.GET[Var.VIEW_TYPE]
    if request.method == 'GET':
        
        if (view_type == 'sdk') or (view_type == 'community'): 
            #feedback 관련된 객체들0
            feedback_id = request.GET[Var.FEEDBACK_ID]
            feedback_info = Feedback.objects.get(seq = feedback_id).feedbackcontext_set.all().select_related()
            feedback_info = feedback_info[0]
            feedback = feedback_info.feedback
            
            device_key = feedback.appuser.pk
            
            feedback_vote_num = feedback.feedbackvote_set.all().count()
            setattr(feedback_info,'vote_num', feedback_vote_num)
            
            feedback_comments = feedback.feedbackcomment_set.all().select_related()
            feedback_comments_num = feedback_comments.count()
            feedback_replys = feedback.reply_set.all().select_related()
            
            for reply in feedback_replys:
                vote_num=reply.replyvote_set.all().count()
                setattr(reply,"vote_num",vote_num)
                comment_list = reply.replycomment_set.all().select_related()
                comment_count = comment_list.count()
                setattr(reply,"comment_list",comment_list)
                setattr(reply,"comment_count",comment_count)
                print reply.pk
                
            
            template = ''   
            if view_type == 'sdk' :
                template = 'sdk/feedback_detail.html'
                return render_to_response(template, {
                                                     'feedback_info':feedback_info,
                                                     'feedback_comments':feedback_comments,
                                                     'feedback_comments_num':feedback_comments_num,
                                                     'feedback_replys':feedback_replys,
                                                     'device_key':device_key   
                                                     },
                                      context_instance=RequestContext(request))
            elif view_type == 'community':
                
                template = 'community/feedback_detail.html'
                '''
                session_key = request.GET['session_key']
                session = Session.objects.get(session_key=session_key)
                uid = session.get_decoded().get('_auth_user_id')
                user = User.objects.get(pk=uid)
                print user.username, user.get_full_name(), user.email
                '''
                
                #username 추가해야함
                return render_to_response(template, {
                                                     'feedback_info':feedback_info,
                                                     'feedback_comments':feedback_comments,
                                                     'feedback_comments_num':feedback_comments_num,
                                                     'feedback_replys':feedback_replys,
                                                     'device_key':device_key 
                                                     },
                                      context_instance=RequestContext(request))
            
            
            
        elif (view_type == 'feedback_comment'):
            try:
                device_key = ''
                feedback_id = request.GET[Var.FEEDBACK_ID]
                contents = request.GET[Var.CONTENTS]
                #device_key가 존재하면 sdk 에서 다는것
                if request.GET.has_key('device_key'):
                    device_key = request.GET['device_key']
                #community 인경우
                else :
                    user = request.user
                    appuser = AppUser.objects.get(user = user)
                    device_key = appuser.device_key
                    
                
                fc=FeedbackComment.objects.create(appuser_id = device_key, feedback_id = feedback_id, contents = contents)
                fc_num = FeedbackComment.objects.filter(feedback__pk = feedback_id).count()
                
                feedback=Feedback.objects.get(pk = feedback_id)
                mygcm.send_push(feedback.appuser.reg_id, fc)
                
                return HttpResponse(str(fc_num)) 
            except Exception, e:
                print str(e) 
        
        elif (view_type == 'reply_comment'):
            device_key = ''
            #device_key가 존재하면 sdk 에서 다는것
            if request.GET.has_key('device_key'):
                device_key = request.GET['device_key']
            #community 인경우
            else :
                user = request.user
                appuser = AppUser.objects.get(user = user)
                device_key = appuser.device_key
                                         
            reply_id = request.GET['reply_id']
            contents = request.GET[Var.CONTENTS]
            
            rc=ReplyComment.objects.create(appuser_device_key = device_key, reply_id = reply_id, contents = contents)
            rc_num = ReplyComment.objects.filter(reply__pk = reply_id).count()
            
            reply = Reply.objects.get(feedback__pk = feedback)
            mygcm.send_push(reply.feedback.appuser.reg_id, rc)
            return HttpResponse(str(rc_num)) 
        
    

    
       
            
            
    
'''

''' 

def view_feedbacks(request):

    if request.method == 'GET':
        
        try :
            request_type = request.GET['request_type']
        except Exception, e:
            request_type = 'view_feedback'
            
        
        if request_type == 'ajax_request':
            try :
                app_id = request.GET[Var.APP_ID]
                last_seq = request.GET['last_seq']
                last_seq = int(last_seq) -1
                              
                category = request.GET[Var.CATEGORY]
                feedbacks = FeedbackContext.objects.all().select_related().filter(feedback__category = category, feedback__app__app_id = app_id, feedback__seq__gt = last_seq)
                #return HttpResponse(json.dumps(Var.todict(feedbacks) ,cls=DjangoJSONEncoder), mimetype="application/json")
                
                return render_to_response('community/more_feedback.html', {
                                                     'feedbacks':feedbacks,
                                                     'app_id':app_id,
                                                     'category':category,   
                                                     },
                                      context_instance=RequestContext(request))
            except  Exception, e:
                print "exception : " + str(e)
        else :
            app_id = request.GET[Var.APP_ID]
            category = request.GET[Var.CATEGORY]          
            
            feedbacks = FeedbackContext.objects.all().select_related().filter(feedback__category = category, feedback__app__app_id = app_id)[:2]
            #feedbacks = FeedbackContext.objects.all().select_related().all()
            
            return render_to_response('community/feedbacks.html', {
                                                     'feedbacks':feedbacks,
                                                     'app_id':app_id,
                                                     'category':category,   
                                                     },
                                      context_instance=RequestContext(request))
'''

session_key = '8cae76c505f15432b48c8292a7dd0e54'

session = Session.objects.get(session_key=session_key)
uid = session.get_decoded().get('_auth_user_id')
user = User.objects.get(pk=uid)

print user.username, user.get_full_name(), user.email

'''   
@csrf_exempt
def view_write_reply(request):
    print "asfasdfasdf"
    if request.method == 'GET':
        feedback_id = request.GET[Var.FEEDBACK_ID]
        print feedback_id
        return render_to_response('community/reply.html', {
                                                 'feedback_id':feedback_id,
                                                 },
                                  context_instance=RequestContext(request))
        
    elif request.method == 'POST':
        try :
            reply_contents = request.POST['reply_contents']
            feedback_id = request.GET[Var.FEEDBACK_ID]
            

            user = request.user            
            print feedback_id
            print user.username
            
            feedback = Feedback.objects.get(seq = feedback_id)
            print feedback.contents
            appuser = AppUser.objects.get(user = user)
            
            reply = Reply(appuser = appuser, feedback = feedback, contents = reply_contents)
            reply.save()
                            
            #feedback 관련된 객체들0
            feedback_info = Feedback.objects.get(seq = feedback_id).feedbackcontext_set.all().select_related()
            feedback_info = feedback_info[0]
            feedback = feedback_info.feedback
            
            feedback_vote_num = feedback.feedbackvote_set.all().count()
            setattr(feedback_info,'vote_num', feedback_vote_num)
            
            feedback_comments = feedback.feedbackcomment_set.all().select_related()
            feedback_comments_num = feedback_comments.count()
            feedback_replys = feedback.reply_set.all().select_related()
            
            for reply in feedback_replys:
                vote_num=reply.replyvote_set.all().count()
                setattr(reply,"vote_num",vote_num)
                comment_list = reply.replycomment_set.all().select_related()
                comment_count = comment_list.count()
                setattr(reply,"comment_list",comment_list)
                setattr(reply,"comment_count",comment_count)
            
            
            
            mygcm.send_push(feedback.appuser.reg_id, reply)
    
        except Exception, e:
            print "exception : " + str(e)        
        
        return render_to_response('community/feedback_detail.html', {
                                                 'feedback_info':feedback_info,
                                                 'feedback_comments':feedback_comments,
                                                 'feedback_comments_num':feedback_comments_num,
                                                 'feedback_replys':feedback_replys   
                                                 },
                                  context_instance=RequestContext(request)) 
'''
####################################################################################
community부분
####################################################################################
'''  
        
        
        
        
        

        

        
        
        
        
        
    


         
        
        
        



