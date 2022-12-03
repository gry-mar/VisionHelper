package edu.ib.visionhelper.call

class CallListElement(var contactName: String, var contactNumber: Long) {

    override fun toString(): String {
        return "CallListElement(contactName='$contactName', contactNumber=$contactNumber)"
    }
}