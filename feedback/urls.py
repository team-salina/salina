from django.conf.urls.defaults import patterns, include, url


# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('feedback.views',
    
    url(r'^feedback/question/$','question'),
    url(r'^feedback/suggestion/$','suggestion'),
    url(r'^feedback/report/$','report'),
    url(r'^feedback/praise/$','praise'),
   
)
