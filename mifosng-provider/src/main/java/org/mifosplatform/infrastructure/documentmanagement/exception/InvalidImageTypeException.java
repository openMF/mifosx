/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.documentmanagement.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * Runtime exception for invalid image types
 */
public class InvalidImageTypeException extends AbstractPlatformResourceNotFoundException {

    public InvalidImageTypeException(String imageType) {
        super("error.documentmanagement.imagetype.invalid", "Image type not supported: " + imageType, imageType);
    }
}
