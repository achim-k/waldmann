/*
 *
 * Copyright (c) 2009 by Christian Dietrich <stettberger@dokucocde.de>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * For more information on the GPL, please go to:
 * http://www.gnu.org/copyleft/gpl.html
 */

/* This PAM Module does authentificate against a preconfigures
 * username/password pair.
 */

#include <avr/pgmspace.h>
#include "pam_prototypes.h"
#include "config.h"

void 
pam_auth(char *username, char *password, uint8_t *auth_state)
{
  if ((strcmp_P(username, PSTR(PAM_SINGLE_USERNAME)) != 0)
      || (strcmp_P(password, PSTR(PAM_SINGLE_PASSWORD)) != 0))
    *auth_state = PAM_DENIED;
  else
    *auth_state = PAM_SUCCESS;
}
