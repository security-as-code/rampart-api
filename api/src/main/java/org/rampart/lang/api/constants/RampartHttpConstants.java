package org.rampart.lang.api.constants;

import org.rampart.lang.api.RampartConstant;
import org.rampart.lang.api.http.RampartHttpFeaturePattern;
import org.rampart.lang.api.http.RampartHttpIOType;
import org.rampart.lang.api.http.RampartHttpValidationType;
import org.rampart.lang.api.http.matchers.RampartHttpMethodMatcher;

/**
 * Class to store constant values specific to Rampart http rules
 */
public final class RampartHttpConstants extends RampartGeneralConstants {
    private RampartHttpConstants() {}

    // Different HTTP request methods
    public static final RampartConstant GET_KEY = RampartHttpMethodMatcher.GET.getName();
    public static final RampartConstant POST_KEY = RampartHttpMethodMatcher.POST.getName();

    public static final RampartConstant COOKIES_KEY = RampartHttpValidationType.COOKIES.getName();
    public static final RampartConstant HEADERS_KEY = RampartHttpValidationType.HEADERS.getName();
    public static final RampartConstant PARAMETERS_KEY = RampartHttpValidationType.PARAMETERS.getName();

    public static final RampartConstant REQUEST_KEY = RampartHttpIOType.REQUEST.getName();
    public static final RampartConstant RESPONSE_KEY = RampartHttpIOType.RESPONSE.getName();
    public static final RampartConstant PATHS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "paths";
        }
    };
    public static final RampartConstant IS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "is";
        }
    };
    public static final RampartConstant PATH_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "path";
        }
    };
    public static final RampartConstant OMITS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "omits";
        }
    };

    public static final RampartConstant VALIDATE_KEY = RampartHttpFeaturePattern.INPUT_VALIDATION.getDeclarationTerm();
    public static final RampartConstant OPEN_REDIRECT_KEY = RampartHttpFeaturePattern.OPEN_REDIRECT.getDeclarationTerm();
    public static final RampartConstant AUTHENTICATE_KEY = RampartHttpFeaturePattern.SESSION_FIXATION.getDeclarationTerm();
    public static final RampartConstant USER_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "user";
        }
    };
    public static final RampartConstant SUBDOMAINS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "subdomains";
        }
    };

    public static final RampartConstant CSRF_KEY = RampartHttpFeaturePattern.CSRF.getDeclarationTerm();
    public static final RampartConstant SYNCHRONIZED_TOKENS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "synchronized-tokens";
        }
    };
    public static final RampartConstant EXCLUDE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "exclude";
        }
    };
    public static final RampartConstant METHOD_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "method";
        }
    };
    public static final RampartConstant TOKEN_TYPE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "token-type";
        }
    };
    public static final RampartConstant TOKEN_NAME_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "token-name";
        }
    };
    public static final RampartConstant AJAX_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "ajax";
        }
    };
    public static final RampartConstant UNIQUE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "unique";
        }
    };
    public static final RampartConstant SHARED_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "shared";
        }
    };
    public static final RampartConstant NO_VALIDATE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "no-validate";
        }
    };
    public static final RampartConstant SAME_ORIGIN_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "same-origin";
        }
    };
    public static final RampartConstant HOSTS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "hosts";
        }
    };

    public static final RampartConstant XSS_KEY = RampartHttpFeaturePattern.XSS.getDeclarationTerm();
    public static final RampartConstant HTML_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "html";
        }
    };
    public static final RampartConstant POLICY_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "policy";
        }
    };
    public static final RampartConstant STRICT_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "strict";
        }
    };
    public static final RampartConstant LOOSE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "loose";
        }
    };

    // replaced by constant `paths`
    @Deprecated
    public static final RampartConstant URI_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "uri";
        }
    };

    // replaced by constant `cookies`
    @Deprecated
    public static final RampartConstant COOKIE_KEY = RampartHttpValidationType.HTTP_COOKIE.getName();

    // replaced by constant `parameters`
    @Deprecated
    public static final RampartConstant PARAMETER_KEY = RampartHttpValidationType.HTTP_PARAMETER.getName();

    // replaced by constant `headers`
    @Deprecated
    public static final RampartConstant HEADER_KEY = RampartHttpValidationType.HTTP_HEADER.getName();

    // replaced by constant `is`
    @Deprecated
    public static final RampartConstant ENFORCE_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "enforce";
        }
    };

    // constant is not being used in the layout of the Http input validation feature
    @Deprecated
    public static final RampartConstant ORIGINS_KEY = new RampartConstant() {
        @Override
        public String toString() {
            return "origins";
        }
    };
}
