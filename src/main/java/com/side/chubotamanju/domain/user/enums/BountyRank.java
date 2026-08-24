package com.side.chubotamanju.domain.user.enums;

import lombok.Getter;

@Getter
public enum BountyRank {

    F("F", "견습", 0, 10),
    E("E", "초급", 100, 20),
    D("D", "중급", 250, 30),
    C("C", "상급", 450, 40),
    B("B", "정예", 700, 55),
    A("A", "마스터", 1000, 70);

    private final String code;
    private final String displayName;
    private final int requiredContribution;
    private final int contributionReward;

    BountyRank(String code, String displayName, int requiredContribution, int contributionReward) {
        this.code = code;
        this.displayName = displayName;
        this.requiredContribution = requiredContribution;
        this.contributionReward = contributionReward;
    }

    public static BountyRank fromCode(String value) {
        for (BountyRank rank : values()) {
            if (rank.code.equalsIgnoreCase(value)) {
                return rank;
            }
        }
        throw new IllegalArgumentException("알 수 없는 랭크 코드야: " + value);
    }

    public BountyRank next() {
        int idx = ordinal() + 1;
        if (idx >= values().length) {
            return null;
        }
        return values()[idx];
    }

    public boolean isHigherThan(BountyRank other) {
        return ordinal() > other.ordinal();
    }
}
