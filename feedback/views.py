# Create your views here.
from django.template import RequestContext
from django.http import HttpResponse
from django.shortcuts import get_object_or_404, render_to_response
from django.utils import timesince
from FeedbackSolution.feedback.models import Evaluation, Feedback, Reply, Vote
from django.core import serializers
import logging

#suggest idea or vote an idea
def suggestion(request):
    #raw_data = None
    """
    print "sex"
    logger = logging.getLogger(__name__)
    logger.info("this is suggestion method")
    #create suggestion
    if request.method == 'POST':
        #get data from POST Entity
        logger.info("this is post method")
        user_id = request.POST['user_id']
        device_key = request.POST['user_key']
        category = 2
        app_id = request.POST['app_id']
        contents = request.POST['contents']
        #save data at db
        feed = Feedback(user_id, device_key, category, app_id, contents)
        feed.save()
        #save vote data
        vote = Vote(feed.pk,feed.category,0)
        vote.save()
        raw_data = feed
    #update vote    
    elif request.method == 'PUT':
        #get PK from PUT method
        feedback_pk = request.PUT['feedback_pk']        
        #modify score
        vote = Vote.objects.get(vote__post_category = feedback_pk)
        vote.vote_score = vote.vote_score + 1
        vote.save()
        raw_data = vote
    #return json data which is received data
    """
    return_data = serializers.serialize('json', 0)
    return HttpResponse(return_data, mimeType='application/json')


        
    
        
         
    