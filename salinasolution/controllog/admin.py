 #!/usr/bin/python
# -*- coding: utf-8 -*-
from salinasolution.systemfeedback.models import Crash, DeviceInfo, Session
from django.contrib import admin

'''
Created on 2013. 3. 7.

@author: doo hyung
'''

admin.site.register(Crash)
admin.site.register(Session)
admin.site.register(DeviceInfo)
