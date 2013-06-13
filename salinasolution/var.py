 #!/usr/bin/python
# -*- coding: utf-8 -*-
'''
Created on 2013. 3. 7.

@author: du hyeong
'''
def todict(obj, classkey=None):
    if isinstance(obj, dict):
        for k in obj.keys():
            obj[k] = todict(obj[k], classkey)
        return obj
    elif hasattr(obj, "__iter__"):
        return [todict(v, classkey) for v in obj]
    elif hasattr(obj, "__dict__"):
        data = dict([(key, todict(value, classkey)) 
            for key, value in obj.__dict__.iteritems() 
            if not callable(value) and not key.startswith('_')])
        if classkey is not None and hasattr(obj, "__class__"):
            data[classkey] = obj.__class__.__name__
        return data
    else:
        return obj
        


# this variable for categories
QUESTION = 'QUESTION'
SUGGESTION = 'SUGGESTION'
PROBLEM = 'PROBLEM'
PRAISE = 'PRAISE'
    
    
CATEGORIES = (
                  (QUESTION, 'question'),
                  (SUGGESTION, 'suggestion'),
                  (PROBLEM, 'problem'),
                  (PRAISE, 'praise'),
                  )
    
    # this variable for key_mapping
    
FEEDBACK_ID = "feedback_id"
USER_ID = "user_id"
DEVICE_KEY = "device_key"
APP_ID = "app_id"
CONTENTS = "contents"
PRAISE_SCORE = "prase_score"
CATEGORY = "category"
# this variable for main_admin_page
MANAGER_ID = "manager_id"
    
# this is for controllog
    
ACTIVITY_NAME = "activity_name"
START_TIME = "start_time"
END_TIME = "END_TIME"
    
OS_VERSION = "OS_VERSION"
DEVICE_NAME = "DEVICE_NAME"
COUNTRY = "COUNTRY"
APP_VERSION = "APP_VERSION"
CREATE_DATE = "CREATE_DATE"
    
EXCEPTION_NAME = "exception_name"
STACKTRACE = "STACKTRACE"
METHOD_NAME = "METHOD_NAME"
LINE_NUMBER = "LINE_NUMBER"
OCCUR_TIME = "OCCUR_TIME"
    
# ETC
INIT_SCORE = 0
    
# data type에 따라 cache에 저장할 데이터 구분
REDIS_KEY_SESSION = 'Session'
REDIS_KEY_DEVICE_INFO = 'DeviceInfo'
REDIS_KEY_SCREEN_FLOW = 'ScreenFlow'
REDIS_KEY_EVENT = 'Event'

#redis관련
REDIS_PORT_NUM = 6379
REDIS_DATA_NEW = 'NEW'
REDIS_DATA_OLD = 'OLD'

#base data
FOCUS_VAR = 'FOCUS_VAR'
COMPOSITE_VAR = 'COMPOSITE_VAR'
ADVANCED_TYPE = 'ADVANCED_TYPE'
SYSTEM_SYSTEM_TYPE = 'SYSTEM_SYSTEM_TYPE'
SYSTEM_USER_TYPE = 'SYSTEM_USER_TYPE'

DESTINATION_ACTIVITY = 'DESTINATION_ACTIVITY'

VIEW_TYPE = 'view_type'




class Dummy: pass



    

