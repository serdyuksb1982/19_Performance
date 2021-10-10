import java.util.Date;
import java.util.TreeSet;

public class WorkTime
{
    private TreeSet<TimePeriod> periods;

    /**
     * Set of TimePeriod objects
     */
    public WorkTime()
    {
        periods = new TreeSet<>();
    }

    public void addVisitTime(long visitTime)
    {
        Date visit = new Date(visitTime);
        TimePeriod newPeriod = new TimePeriod(visit, visit);
        for(TimePeriod period : periods)
        {
            if(period.compareTo(newPeriod) == 0)
            {
                period.appendTime(visit);
                return;
            }
        }
        periods.add(new TimePeriod(visit, visit));
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder ();

        for(TimePeriod period : periods)
        {
            if(!builder.isEmpty()) {
                builder.append ( ", " );
            }
            builder.append ( period.toString () );

        }
        return builder.toString ();
    }
}
