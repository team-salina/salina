 #!/usr/bin/python
# -*- coding: utf-8 -*-
'''
Created on 2013. 3. 7.

@author: du hyeong
'''


def get_default_result():
    str = """
{ "nodeDataArray": [

    { "key": 1, "text": "B" },
    { "key": 2, "text": "C" },
    { "key": 3, "text": "D" },
    { "key": 4, "text": "E" },
    { "key": 5, "text": "F" },
    { "key": 6,  "text": "G!" },
    { "key": 7, "text": "H!" },
    { "key": 8, "text": "H+" },

    { "key": 9, "text": "I" },
    { "key": 10, "text": "J" },
    { "key": 11, "text": "L" },
    { "key": 12, "text": "M" },
    { "key": 13, "text": "N" },
    { "key": 14, "text": "O" },
    { "key": 15,  "text": "P!" },
    { "key": 16, "text": "Q!" }


  ],
  "linkDataArray": [

    { "from":  0, "to": 1 },
    { "from":  1, "to": 2 },
    { "from":  2, "to": 3 },
    { "from":  3, "to": 4 },
    { "from":  4, "to": 16 },

    { "from":  5, "to": 6 },
    { "from":  6, "to": 7 },
    { "from":  7, "to": 8, "text":"100" },
    { "from":  8, "to": 16 },

  { "from":  0, "to": 9},
    { "from":  9, "to": 10 },
    { "from":  10, "to": 11 },
    { "from":  11, "to": 12 },
    { "from":  12, "to": 16 },

    { "from":  13, "to": 14 },
    { "from":  14, "to": 15 },
    { "from":  15, "to": 16 }
  ]
}
    """
    return str





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
QUESTION = 'question'
SUGGESTION = 'suggestion'
PROBLEM = 'probelm'
PRAISE = 'evaluation'
    
    
CATEGORIES = (
                  (QUESTION, 'question'),
                  (SUGGESTION, 'suggestion'),
                  (PROBLEM, 'problem'),
                  (PRAISE, 'evaluation'),
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
FOCUS = 'focus'
COMPOSITE = 'composite'
ADVANCED_TYPE = 'ADVANCED_TYPE'
SYSTEM_SYSTEM_TYPE = 'SYSTEM_SYSTEM_TYPE'
SYSTEM_USER_TYPE = 'SYSTEM_USER_TYPE'

DESTINATION_ACTIVITY = 'DESTINATION_ACTIVITY'

VIEW_TYPE = 'view_type'




class Dummy: pass



    

