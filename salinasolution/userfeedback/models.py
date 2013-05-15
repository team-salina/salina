 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db import models
from random import choice
from django.db.models.base import Model
from compiler.ast import Mod
from salinasolution.userinfo.models import User
from django.db.models.deletion import CASCADE
import salinasolution.var as var
# Create your models here.


'''
Created on 2013. 3. 7.

@author: doo hyung

사용자가 올린글을 db에 저장하고, 저장한 글에 대한 답글 댓글, 칭찬 평점을 저장하는 모델이다.
'''


'''
Feedback 테이블은 사용자가 올린 피드백을 저장한다.
피드백은 총 질문, 제안, 문제, 칭찬 이렇게 네가지이며, 
이것은 category를 기반으로 구분하기 된다.
solved check는 해당 데이터의 해결 여부를 나타내고, 
reply_num은 답글의 갯수를 나타낸다.   
'''
class Feedback(models.Model):
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    #category = models.IntegerField(default = 0,choices = Var.CATEGORIES)
    category = models.CharField(max_length = 50, choices = var.CATEGORIES)
    pub_date = models.DateTimeField(auto_now_add = True)
    contents = models.TextField()
    solved_check = models.BooleanField(default = False)
    reply_num = models.IntegerField(default = 0)
    
    
    '''
    auto_save_by_property 메서드는 해당 Feedback의 컬럼값을 넣고 저장하게 되면, foreignkey로 있는 user를 자동적으로 저장하고,(없는 경우는 읽어옴) 
    user데이터를 사용하여 feedback 테이블에도 저장하게 된다.
    '''
    def auto_save_by_property(self, user_id, device_key, app_id, category, contents):
        feed = Feedback(user = User().auto_save(user_id, device_key), category = category, contents = contents)
        feed.save()
        return feed
    
    '''
     auto_save_by_property와 같은 행위를 하지만 feedback 인스턴스를 받아서 수행한다. 
    '''
    def auto_save_by_object(self, feed_obj):
        feed = Feedback().auto_save_by_property(feed_obj.user.user_id, feed_obj.user.device_key, feed_obj.app_id, feed_obj.category, feed_obj.contents)
        feed.save()
        return feed
    
    def auto_save(self):
        feed = Feedback(user = User().auto_save(self.user_id, self.device_key), category = self.category, contents = self.contents, app_id = self.app_id)
        feed.save()
        return feed 
    
'''
 feedback 테이블을 foreignkey로 참조하고 있다. (하나의 feedback에 대해서 여러개의  답글(reply)가 있을 수 있기 때문에)
 adopted_check는 해당 feedback에 대해서 채택됬는지 여부를 나타낸다.
'''
class Reply(models.Model):
    
    user = models.ForeignKey(User)
    feedback_id = models.IntegerField()
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)
    adopted_check = models.BooleanField(default = False)
 
 
'''
 feedback에 대한 댓글을 나타낸다.
'''
class FeedbackComment(models.Model):
    
    user = models.ForeignKey(User)
    feedback_id = models.IntegerField()
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)

'''
reply에 대한 댓글을 나타낸다.
'''   
class ReplyComment(models.Model):
    
    user = models.ForeignKey(User)
    reply_id = models.IntegerField()
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)
    
'''
feedback에 대해서 vote(투표)한 리스트의 사람들을 나타낸다.
'''
class FeedbackVote(models.Model):
    
    user = models.ForeignKey(User)                    
    feedback_id = models.IntegerField()

'''
reply에 대해서 vote(투표)한 리스트의 사람들을 나타낸다.
'''  
class ReplyVote(models.Model):
    
    user = models.ForeignKey(User)
    reply_id = models.IntegerField()


'''
답글에 대한 평가를 나타낸다. 평가는 피드백을 올린사람만 달 수 있다.
'''       
class ReplyEvaluation(models.Model):
    
    user = models.ForeignKey(User)
    reply_id = models.IntegerField()
    ev_score = models.FloatField()

'''
{u'category': u'SUGGESTION', u'device_key': u'7848f43255e85c015c4f739b38598ed02bc2c91735b6cb195e5bdc460402768d', u'user_id': u'', u'contents': u'\u3141\u3134\u3147\u313b\u3147\u3134\u3139', u'pk': -1}
피드백중 칭찬의 경우  점수가 나타나게 되는데, 그점수는 praise score 테이블에서 나타나게 된다.
praise score는 feedback 테이블을 참조하고 있다.
'''  
class PraiseScore(models.Model):
    
    feedback = models.ForeignKey(Feedback, related_name = 'praisescores')
    praise_score = models.FloatField()
