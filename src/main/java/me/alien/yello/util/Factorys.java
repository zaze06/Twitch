/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello.util;

import me.alien.yello.Main;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.domain.ChannelPointsUser;

public class Factorys {
    public static ChannelPointsRedemption redemptionFactory(EventUser user, Main plugin, String redemptionId){
        return redemptionFactory(user, plugin, redemptionId, 0);
    }
    public static ChannelPointsRedemption redemptionFactory(EventUser user, Main plugin, String redemptionId, int cost){
        ChannelPointsUser cpu = new ChannelPointsUser();
        cpu.setId(user.getId());
        cpu.setLogin(user.getName());
        cpu.setDisplayName(user.getName());

        ChannelPointsReward cpr = new ChannelPointsReward();
        cpr.setCost(cost);

        ChannelPointsRedemption fakeRedemption = new ChannelPointsRedemption();
        fakeRedemption.setId(redemptionId);
        fakeRedemption.setChannelId(Main.credentials.getString("channel_ID"));
        fakeRedemption.setUser(cpu);
        fakeRedemption.setReward(cpr);

        return fakeRedemption;
    }
}
