package edu.ib.visionhelper.call

class CallListElement(var contactName: String, var contactNumber: Int) {

    override fun toString(): String {
        return "CallListElement(contactName='$contactName', contactNumber=$contactNumber)"
    }
}