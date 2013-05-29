 #!/usr/bin/python
# -*- coding: utf-8 -*-
from salinasolution.feedback.models import Feedback, FeedbackComment, FeedbackVote, PraiseScore, Reply, ReplyComment, ReplyEvaluation, ReplyVote
from django.contrib import admin

admin.site.register(Feedback)
admin.site.register(FeedbackComment)
admin.site.register(FeedbackVote)
admin.site.register(PraiseScore)
admin.site.register(Reply)
admin.site.register(ReplyComment)
admin.site.register(ReplyEvaluation)
admin.site.register(ReplyVote)







