package com.iqtransit.agency;

public  class AgencyFactory {

        public static AgencyInterface createAgency(String id) {

                switch (id) {

                    case "MBTA":
                        return new MBTAAgency();  
        
                    case "LIRR":
                        return new LIRRAgency();
                   
                    case "METRA":
                        return new METRAAgency();
                      
                    case "MNR":
                        return new MNRAgency();
                    
                    case "NJRR":
                        return new NJRRAgency();
                 
                     case "SEPTA":
                        return new SEPTAAgency();
                        
                }

                return null;

        }

        public static String[] available() {
            String[] result = { "MBTA", "LIRR", "METRA", "MNR", "NJRR", "SEPTA" };
            return result;
        }

}