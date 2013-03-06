from django.db import models
from django.db.models.base import Model
from django.template.defaultfilters import default

QUESTION = 1
SUGGESTION = 2 
PROBLEM = 3
PRAISE = 4
REPLY = 5
COMMENT = 6

CATEGORIES = (
    (QUESTION, 'Question'),
    (SUGGESTION, 'Suggestion'),
    (PROBLEM, 'Problem'),
    (PRAISE, 'Praise'),
    (REPLY, 'Reply'),
    (COMMENT, 'Comment'),
  )

class Vote(models.Model):
    
    global CATEGORIES
    post_sq = models.IntegerField()
    post_category = models.IntegerField(default = 0, choices = CATEGORIES)
    vote_score = models.IntegerField()
    
class Feedback(models.Model):
    
    global CATEGORIES
    user_id = models.CharField(max_length = 100)
    device_key = models.CharField(max_length = 100)
    category  = models.IntegerField(default = 0, choices = CATEGORIES)
    app_id = models.CharField(max_length = 100)
    pub_date = models.DateField(verbose_name = None, name = None, auto_now = True, auto_now_add = False)
    contents = models.TextField()
    
class Evaluation(models.Model):    
    
    post_sq = models.ForeignKey(Feedback)
    ev_score = models.DecimalField(verbose_name = None, name = None, max_digits = 100, decimal_places = True)
     
class Reply(models.Model):
    
    global CATEGORIES
    post_seq = models.IntegerField()
    post_category = models.IntegerField(default = 0, choices = CATEGORIES)
    category = models.IntegerField(default = 0, choices = CATEGORIES)
    contents = models.TextField()
    user_id = models.CharField(max_length = 100)
    pub_date = models.DateField(verbose_name = None, name = None, auto_now = True, auto_now_add = False)

    
    
    
     
    