  header(network.h)
  init(network_init)
 header(core/periodic.h)
 init(periodic_init)
  header(core/vfs/vfs.h)
  block(Miscelleanous)
  ecmd_feature(d, "d ", ADDR, Dump the memory at ADDR (16 bytes).)
  ecmd_feature(fuse, "fuse",,Display current fuse settings)
  ecmd_feature(hostname, "hostname",,Display hostname.)
  block(Resetting the controller)
  ecmd_feature(reset, "reset",,Reset the Ethersex.)
  ecmd_ifndef(TEENSY_SUPPORT)
    ecmd_feature(wdreset, "wdreset",,Go into endless loop to trigger a watchdog timeout.)
  ecmd_endif()
  timer(1, `
#       if UIP_CONF_IPV6
        if (counter == 5) {
            // Send a router solicitation every 10 seconds, as long
            // as we only got a link local address.  First time one
            // second after boot 
#           ifndef IPV6_STATIC_SUPPORT
            if(((u16_t *)(uip_hostaddr))[0] == HTONS(0xFE80)) {
                uip_router_send_solicitation();
                transmit_packet();
            }
#           endif
        }
#       endif // UIP_CONF_IPV6 
')
  header(hardware/ethernet/enc28j60.h)
  net_init(init_enc28j60)
  mainloop(network_process)
  timer(50, enc28j60_periodic())
  header(hardware/onewire/onewire.h)
  init(onewire_init)
  ifdef(`conf_ONEWIRE_POLLING',`timer(40, ow_periodic())')
  header(protocols/syslog/syslog_net.h)
  net_init(syslog_net_init)
  header(protocols/syslog/syslog.h)
  mainloop(syslog_flush)
  header(protocols/uip/uip.h)
  header(protocols/uip/uip_router.h)
  timer(10, ` 
#       if UIP_CONNS <= 255
            uint8_t i;
#       else
            uint16_t i;
#endif

#           if UIP_TCP == 1
            for (i = 0; i < UIP_CONNS; i++) {
		uip_stack_set_active(uip_conns[i].stack);
                uip_periodic(i);

                // if this generated a packet, send it now 
                if (uip_len > 0)
		    router_output();
            }
#           endif // UIP_TCP == 1

#           if UIP_UDP == 1
            // check udp connections every time 
            for (i = 0; i < UIP_UDP_CONNS; i++) {
		uip_stack_set_active(uip_udp_conns[i].stack);
                uip_udp_periodic(i);

                // if this generated a packet, send it now
                if (uip_len > 0)
		    router_output();
            }
#           endif
  
')
  header(protocols/uip/uip_arp.h)
  timer(500, uip_arp_timer())
  header(protocols/ecmd/via_tcp/ecmd_net.h)
  net_init(ecmd_net_init)

  state_header(protocols/ecmd/via_tcp/ecmd_state.h)
  state_tcp(struct ecmd_connection_state_t ecmd)
  header(services/httpd/httpd.h)
  net_init(httpd_init)

  state_header(services/httpd/httpd_state.h)
  state_tcp(struct httpd_connection_state_t httpd)
  ecmd_feature(io, "io")
  block(Analog/Digital Conversion ([[ADC]]))
  ecmd_feature(adc_get, "adc get", [CHANNEL], Get the ADC value in hex of CHANNEL or if no channel set of all channels.)
  block(Network configuration)
  ecmd_feature(mac, "mac",[xx:xx:xx:xx:xx:xx],Display/Set the MAC address.)
  ecmd_ifdef(DEBUG_ENC28J60)
    ecmd_feature(enc_dump, "enc dump", , Dump the internal state of the enc to serial)
  ecmd_endif()
    
  block([[Dallas_1-wire_Bus]])
  ecmd_ifdef(ONEWIRE_DETECT_SUPPORT)
    ecmd_feature(onewire_list, "1w list",,Return a list of the connected onewire devices)
  ecmd_endif()
  ecmd_feature(onewire_get, "1w get", DEVICE, Return temperature value of onewire DEVICE (provide 64-bit ID as 16-hex-digits))
  ecmd_feature(onewire_convert, "1w convert", [DEVICE], Trigger temperature conversion of either DEVICE or all connected devices)
  block(Network configuration)
  ecmd_ifndef(TEENSY_SUPPORT)
    ecmd_ifdef(UIP_SUPPORT)
      ecmd_ifndef(IPV6_SUPPORT)
	ecmd_feature(netmask, "netmask",[IP],Display/Set the network mask.)
      ecmd_endif()

      ecmd_feature(ip, "ip",[IP],Display/Set the IP address.)
      ecmd_feature(gw, "gw",[IP],Display/Set the address of the default router.)
    ecmd_endif()
  ecmd_endif()
block(WTEST)
ecmd_feature(wtest, "wtest",param1, wtest-description)
