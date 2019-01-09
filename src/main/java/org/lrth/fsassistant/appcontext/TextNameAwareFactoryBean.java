package org.lrth.fsassistant.appcontext;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;

public class TextNameAwareFactoryBean implements FactoryBean, BeanNameAware {

    @Override
    public void setBeanName(String s) {

    }

    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
