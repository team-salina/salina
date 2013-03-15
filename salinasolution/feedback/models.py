from django.db import models
from random import choice
from django.db.models.base import Model
from compiler.ast import Mod
from salinasolution.var import Var
from salinasolution.userinfo.models import User
from django.db.models.deletion import CASCADE
# Create your models here.
    
class Feedback(models.Model):
    
    
    user = models.ForeignKey(User)
    app_id = models.CharField(max_length = 50)
    category = models.IntegerField(default = 0,choices = Var.CATEGORIES)
    pub_date = models.DateTimeField(auto_now_add = True)
    contents = models.TextField()
    solved_check = models.BooleanField(default = False)
    reply_num = models.IntegerField(default = 0)
    
    def auto_save_by_property(self, user_id, device_key, app_id, category, contents):
        feed = Feedback(user = User().auto_save(user_id, device_key), category = category, contents = contents)
        feed.save()
        return feed
    
    def auto_save_by_object(self, feed_obj):
        feed = Feedback().auto_save_by_property(feed_obj.user_id, feed_obj.device_key, feed_obj.app_id, feed_obj.category, feed_obj.contents)
        feed.save()
        return feed
    
class Reply(models.Model):
    
    feedback = models.ForeignKey(Feedback)
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)
    adopted_check = models.BooleanField(default = False)
 
 #comment table must know who vote feedback or reply
class FeedbackComment(models.Model):
    
    user = models.ForeignKey(User)
    feedback_id = models.IntegerField()
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)
    
class ReplyComment(models.Model):
    
    user = models.ForeignKey(User)
    reply_id = models.IntegerField()
    contents = models.TextField()
    pub_date = models.DateTimeField(auto_now_add = True)
    
#vote person list each feedback
class FeedbackVote(models.Model):
    
    user = models.ForeignKey(User)                    
    feedback_id = models.IntegerField()

#vote person list each reply    
class ReplyVote(models.Model):
    
    user = models.ForeignKey(User)
    reply = models.IntegerField()
    
class ReplyEvaluation(models.Model):
    
    reply = models.ForeignKey(Reply, related_name = 'replyevaluations')
    ev_score = models.FloatField()

class PraiseScore(models.Model):
    
    feedback = models.ForeignKey(Feedback, related_name = 'praisescores')
    praise_score = models.FloatField()
