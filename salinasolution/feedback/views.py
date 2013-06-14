 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.views.decorators.csrf import csrf_exempt, csrf_protect
from salinasolution.debug import debug
import simplejson as json
import  salinasolution.var as Var
from salinasolution.controllog.models import Session, DeviceInfo
from salinasolution.feedback.models import Feedback, FeedbackContext, Reply
from salinasolution.userinfo.models import App,  AppUser
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext
from salinasolution import pson
import salinasolution.redisread as redisread
import redis 
import salinasolution.ast as ast
from django.http import  HttpResponseRedirect
import mimetypes
from django.core.serializers.json import DjangoJSONEncoder


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
        '''
        return "success"
         
    return "fail" 

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

    
def view_feedback_detail(request):

    if request.method == 'GET':
        feedback_id = request.GET[Var.FEEDBACK_ID]
        view_type = request.GET[Var.VIEW_TYPE]
        
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
        
        template = ''   
        if view_type == 'sdk' :
            template = 'sdk/feedback_detail.html'
        elif view_type == 'community':
            template = 'community/feedback_detail.html'
        
        return render_to_response(template, {
                                                 'feedback_info':feedback_info,
                                                 'feedback_comments':feedback_comments,
                                                 'feedback_comments_num':feedback_comments_num,
                                                 'feedback_replys':feedback_replys   
                                                 },
                                  context_instance=RequestContext(request))
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
        
        
        
        
        

        

        
        
        
        
        
    


         
        
        
        


