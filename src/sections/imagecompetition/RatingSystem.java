package sections.imagecompetition;

public class RatingSystem {
    public static double DEFAULT_RATING = 1200.0;
    private static double K = 64;

    public static void match(ImageContestant im1, ImageContestant im2, MatchResult matchResult) {
        double rating1 = im1.getRating();
        double rating2 = im2.getRating();


        double qa = Math.pow(10, rating1/400);
        double qb = Math.pow(10, rating2/400);

        double ea = qa / (qa + qb);
        double eb = qb / (qa + qb);

        double sa = 0;
        double sb = 0;

        switch (matchResult) {
            case victory:
                sa = 1;
                sb = 0;
                break;
            case tie:
                sa = 0.5;
                sb = 0.5;
                break;
            case defeat:
                sa = 0;
                sb = 1;
                break;
        }

        double newRating1 = rating1 + K * (sa - ea);
        double newRating2 = rating2 + K * (sb - eb);

        im1.setRating(newRating1);
        im2.setRating(newRating2);
    }

    public enum MatchResult {
        victory, tie, defeat
    }
}
