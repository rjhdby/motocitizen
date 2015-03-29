package motocitizen.maps.general;

import android.content.Context;
import android.location.Location;

    public abstract class MCMap {

        private String name;

        public abstract void placeUser(Context context);

        public abstract void jumpToPoint(Location location);

        public abstract void zoom(int zoom);

        public abstract void placeAcc(Context context);

        public String getName() {
            return name;
        }

        protected void setName(String name) {
            this.name = name;
        }
   }

