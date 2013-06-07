 #!/usr/bin/python
# -*- coding: utf-8 -*-
from django.db import models
from random import choice
from django.db.models.base import Model
from compiler.ast import Mod
from salinasolution.userinfo.models import AppUser, App
from django.db.models.deletion import CASCADE
import salinasolution.var as var

# Create your models here.

#jsonpickle.
'''
Created on 2013. 3. 7.

@author: doo hyung

사용자가 올린글을 db에 저장하고, 저장한 글에 대한 답글 댓글, 칭찬 평점을 저장하는 모델이다.
'''
'''
class DeviceInfo(models.Model):
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    os_version = models.CharField(max_length=50)
    device_name = models.CharField(max_length=50)
    country = models.CharField(max_length=50)
    app_version = models.CharField(max_length=50)
    create_date = models.DateTimeField(auto_now_add=True)
    
    def auto_save(self):
        self.user = User().aut9   _save(self.user_id, self.device_key)
        self.save()
        return self
'''

'''
Feedback 테이블은 사용자가 올린 피드백을 저장한다.
피드백은 총 질문, 제안, 문제, 칭찬 이렇게 네가지이며, 
이것은 category를 기반으로 구분하기 된다.
solved check는 해당 데이터의 해결 여부를 나타내고, 
reply_num은 답글의 갯수를 나타낸다.   
'''
class Feedback(models.Model):
    
    seq = models.AutoField(primary_key = True)
    
    appuser = models.ForeignKey(AppUser)
    app = models.ForeignKey(App)
    
    category = models.CharField(max_length = 50, choices = var.CATEGORIES)
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)
    
    solved_check = models.BooleanField(default = False)
    reply_num = models.IntegerField(default = 0)
    
    
    
    
    
    '''
    auto_save_by_property 메서드는 해당 Feedback의 컬럼값을 넣고 저장하게 되면, foreignkey로 있는 user를 자동적으로 저장하고,(없는 경우는 읽어옴) 
    user데이터를 사용하여 feedback 테이블에도 저장하게 된다.
    '''
    def auto_save_by_property(self, user_id, device_key, app_id, category, contents):
        feed = Feedback(user = AppUser().auto_save(user_id, device_key), category = category, contents = contents)
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
        feed = Feedback(user = AppUser().auto_save(self.user_id, self.device_key), category = self.category, contents = self.contents, app_id = self.app_id)
        feed.save()
        return feed 
    
'''
 feedback 테이블을 foreignkey로 참조하고 있다. (하나의 feedback에 대해서 여러개의  답글(reply)가 있을 수 있기 때문에)
 adopted_check는 해당 feedback에 대해서 채택됬는지 여부를 나타낸다.
'''
class Reply(models.Model):
    
    appuser = models.ForeignKey(AppUser)
    feedback = models.ForeignKey(Feedback)
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)
    adopted_check = models.BooleanField(default = False)
    
    def save(self, *args, **kwargs):
    #... other code here
        self.feedback.reply_num= self.feedback.reply_num + 1
        self.feedback.save()
        super(Reply, self).save(*args, **kwargs)
        
        
        
    def set_adopted(self):
        self.adopted_check = True
        self.feedback.solved_check = True
        self.feedback.save()
        self.save()
 
 
'''
 feedback에 대한 댓글을 나타낸다.
'''
class FeedbackComment(models.Model):
    
    appuser = models.ForeignKey(AppUser)
    feedback = models.ForeignKey(Feedback)
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)

'''
reply에 대한 댓글을 나타낸다.
'''   
class ReplyComment(models.Model):
    
    appuser = models.ForeignKey(AppUser)
    reply = models.ForeignKey(Reply)
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)
    
'''
feedback에 대해서 vote(투표)한 리스트의 사람들을 나타낸다.
'''
class FeedbackVote(models.Model):
    
    appuser = models.ForeignKey(AppUser)                   
    feedback = models.ForeignKey(Feedback)
    
    class Meta:
        unique_together = ('appuser','feedback')

'''
reply에 대해서 vote(투표)한 리스트의 사람들을 나타낸다.
'''  
class ReplyVote(models.Model):
    
    appuser = models.ForeignKey(AppUser)                   
    reply = models.ForeignKey(Reply)
    
    class Meta:
        unique_together = ('appuser','reply')


'''
답글에 대한 평가를 나타낸다. 평가는 피드백을 올린사람만 달 수 있다.
'''       
class ReplyEvaluation(models.Model):
    
    user = models.ForeignKey(AppUser)
    reply = models.ForeignKey(Reply)
    ev_score = models.FloatField()

'''
{u'category': u'SUGGESTION', u'device_key': u'7848f43255e85c015c4f739b38598ed02bc2c91735b6cb195e5bdc460402768d', u'user_id': u'', u'contents': u'\u3141\u3134\u3147\u313b\u3147\u3134\u3139', u'pk': -1}
피드백중 칭찬의 경우  점수가 나타나게 되는데, 그점수는 praise score 테이블에서 나타나게 된다.
praise score는 feedback 테이블을 참조하고 있다.
'''  
class PraiseScore(models.Model):
    
    feedback = models.ForeignKey(Feedback)
    praise_score = models.FloatField()
    
'''
Feedback의 Context를 저장하기 위한 데이타
'''
class FeedbackContext(models.Model):
    
    feedback = models.ForeignKey(Feedback)
    app_version = models.CharField(max_length = 50)
    
    device_model = models.CharField(max_length = 50)
    device_manufacturer = models.CharField(max_length = 50)
    device_country = models.CharField(max_length = 50)
    
    screen_name = models.CharField(max_length = 50)
    function_name = models.CharField(max_length = 50)
    
    locale_language = models.CharField(max_length = 50)
    locale_country = models.CharField(max_length = 50)
    
    os_version = models.CharField(max_length = 50)
    
    network_carrier = models.CharField(max_length = 50)
    network_type = models.CharField(max_length = 50)
    
    latitude = models.FloatField()
    longitude = models.FloatField()
    
    def auto_save_(self, feedback):
        self.feedback = feedback
        self.latitude = float(self.latitude)
        self.longitude = float(self.longitude)
        self.save()
        return self
    
    def auto_save(self):
        self.latitude = float(self.latitude)
        self.longitude = float(self.longitude)
        self.save()
        return self
    
