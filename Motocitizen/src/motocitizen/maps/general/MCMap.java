package motocitizen.maps.general;

import android.content.Context;
import android.location.Location;

    public abstract class MCMap {

        public static final String OSM = "osm";
        public static final String GOOGLE = "google";

        private String name;

        public abstract void placeUser(Context context);

        public abstract void jumpToPoint(Location location);

        @SuppressWarnings("SameParameterValue")
        public abstract void zoom(int zoom);

        public abstract void placeAcc(Context context);

        public String getName() {
            return name;
        }

        protected void setName(String name) {
            this.name = name;
        }
   }

