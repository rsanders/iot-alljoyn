/*
 * AlljoynBusListener.h
 *
 *  Created on: Feb 14, 2015
 *      Author: hieult
 */

#ifndef ALLJOYNBUSLISTENER_H_
#define ALLJOYNBUSLISTENER_H_

/**
 *
 */
class AlljoynBusListenerImpl : public ajn::BusListener, public ajn::SessionPortListener
{
  private:
    /**
     * The port used as part of the join session request
     */
    ajn::SessionPort m_SessionPort;

  public:
    AlljoynBusListenerImpl();
    virtual ~AlljoynBusListenerImpl();

    /**
     * Set the Value of the SessionPort associated with this SessionPortListener
     * @param sessionPort
     */
    void setSessionPort(ajn::SessionPort sessionPort) {
        m_SessionPort = sessionPort;
    }

    /**
     * Get the SessionPort of the listener
     * @return
     */
    ajn::SessionPort getSessionPort() {
        return m_SessionPort;
    }
};

#endif /* ALLJOYNBUSLISTENER_H_ */
