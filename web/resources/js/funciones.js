/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//<![CDATA[
    /**
     * Faces Validator
     */
    PrimeFaces.validator['custom.emailValidator'] = {
        pattern: /\S+@\S+/,
        validate: function(element, value) {
            //use element.data() to access validation metadata, in this case there is none.
            if(!this.pattern.test(value)) {
                throw {
                    summary: 'Validation Error',
                    detail: value + ' is not a valid email.'
                }
            }
        }
    };
     
    /**
     * Bean validator
     */
    PrimeFaces.validator['Email'] = {
        pattern: /\S+@\S+/,
        MESSAGE_ID: 'org.primefaces.examples.validate.email.message',
        validate: function(element, value) {
            var vc = PrimeFaces.util.ValidationContext;
            if(!this.pattern.test(value)) {
                var msgStr = element.data('p-email-msg'),
                msg = msgStr ? {summary:msgStr, detail: msgStr} : vc.getMessage(this.MESSAGE_ID);
                throw msg;
            }
        }
    };
    //]]>

