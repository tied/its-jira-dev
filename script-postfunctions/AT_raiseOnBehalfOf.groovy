/*
*	@name 	AT_raiseOnBehalfOf.groovy
*	@type		Post function	
*	@brief 	Adds initial Reporter to Request Participants field, and assigns user 
* 				in "Raise on behalf of" field to the Reporter field.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue
import java.util.ArrayList
import com.atlassian.jira.event.type.EventDispatchOption

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

def REQUEST_PARTICIPANTS_FIELD = "customfield_10600"
def RAISE_ON_BEHALF_OF_FIELD = "customfield_12131"

ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
MutableIssue thisIssue = issue

def requestParticipantsField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(REQUEST_PARTICIPANTS_FIELD)
def raiseOnBehalfOfField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(RAISE_ON_BEHALF_OF_FIELD)

ApplicationUser thisReporter = thisIssue.getReporter()
ApplicationUser newReporter = thisIssue.getCustomFieldValue(raiseOnBehalfOfField) as ApplicationUser

/* If "Raise on behalf of" field isn't set, exit and do nothing */
if (!newReporter) { return "No new reporter specified." }

ArrayList<ApplicationUser> requestParticipants = []
requestParticipants.add(thisReporter)

thisIssue.setCustomFieldValue(requestParticipantsField, requestParticipants)
thisIssue.setReporter(newReporter)
thisIssue.setCustomFieldValue(raiseOnBehalfOfField, null)

try {
	ComponentAccessor.getIssueManager().updateIssue(
		currentUser,
		thisIssue,
		EventDispatchOption.ISSUE_UPDATED,
		false
	)
} catch (Exception e) {
	log.debug "Exception: " + e
}